package com.canpay.api.controller.account;

import com.canpay.api.dto.dashboard.operatorassignment.OperatorAssignmentListWithTotalDto;
import com.canpay.api.dto.dashboard.transactions.OwnerWithdrawRequestDto;
import com.canpay.api.dto.dashboard.transactions.WithdrawalTransactionDto;
import com.canpay.api.entity.*;
import com.canpay.api.repository.dashboard.DOperatorAssignmentRepository;
import com.canpay.api.repository.dashboard.DWalletRepository;
import com.canpay.api.service.dashboard.DTransactionService;
import com.canpay.api.service.implementation.JwtService;
import com.canpay.api.repository.BusRepository;
import com.canpay.api.repository.TransactionRepository;
import com.canpay.api.service.implementation.UserServiceImpl;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payment")
public class PaymentController {

    private final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    @Value("${canpay.hmac.passenger-secret}")
    private String passengerHmacSecret;

    private final JwtService jwtService;
    private final UserServiceImpl userService;
    private final BusRepository busRepository;
    private final DOperatorAssignmentRepository operatorAssignmentRepository;
    private final DWalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final MqttClient mqttClient;
    private final DTransactionService ownerWithdrawService;

    public PaymentController(JwtService jwtService, UserServiceImpl userService, BusRepository busRepository,
                             DOperatorAssignmentRepository operatorAssignmentRepository, DWalletRepository walletRepository,
                             TransactionRepository transactionRepository, MqttClient mqttClient, DTransactionService ownerWithdrawService) {
        this.jwtService = jwtService;
        this.userService = userService;
        this.busRepository = busRepository;
        this.operatorAssignmentRepository = operatorAssignmentRepository;
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
        this.mqttClient = mqttClient;
        this.ownerWithdrawService = ownerWithdrawService;
    }

    @PostMapping("/process")
    @PreAuthorize("hasRole('PASSENGER')")
    @Transactional
    public ResponseEntity<?> processPayment(
            @RequestHeader(value = "Authorization") String authHeader,
            @RequestHeader(value = "X-Signature", required = false) String signature,
            @RequestHeader(value = "X-Timestamp", required = false) String timestamp,
            @RequestBody Map<String, String> request) {

        logger.debug("Received payment request: {}", request);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.warn("Authorization header missing or invalid");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "Authorization header with Bearer token is required"));
        }

        // --- HMAC signature verification ---
        if (signature == null || timestamp == null) {
            logger.warn("Missing X-Signature or X-Timestamp header");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "Missing signature or timestamp"));
        }

        String token = authHeader.substring(7);
        String passengerEmail;
        User passenger;
        try {
            passengerEmail = jwtService.extractEmail(token);
            String role = jwtService.extractRole(token);
            if (!"PASSENGER".equals(role)) {
                logger.warn("Invalid role: {}", role);
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("success", false, "message", "Invalid role for payment"));
            }
            // Fetch passenger early for HMAC secret
            passenger = userService.findUserByEmail(passengerEmail)
                    .orElseThrow(() -> {
                        logger.warn("Passenger not found: {}", passengerEmail);
                        return new RuntimeException("Passenger not found");
                    });
        } catch (Exception e) {
            logger.warn("Invalid token: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "Invalid or expired token"));
        }

        // Validate request fields for HMAC
        String amountStr = request.get("amount");
        if (amountStr == null) {
            logger.warn("Missing amount in request for HMAC verification");
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "Amount is required"));
        }

        // --- HMAC verification logic ---
        try {
            String hmacSecret = getHmacSecretForPassenger(passengerEmail); // <-- Replace with your actual logic
            if (hmacSecret == null) {
                logger.warn("No HMAC secret found for passenger: {}", passengerEmail);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("success", false, "message", "No HMAC secret found for user"));
            }
            String payloadToSign = amountStr + ":" + timestamp;
            String computedSignature = computeHmacSha256Base64(hmacSecret, payloadToSign);
            if (!signature.equals(computedSignature)) {
                logger.warn("Invalid HMAC signature. Provided: {}, Computed: {}", signature, computedSignature);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("success", false, "message", "Invalid signature"));
            }
        } catch (Exception e) {
            logger.error("Error verifying HMAC signature: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "Signature verification failed"));
        }

        try {
            // Validate request
            String busIdStr = request.get("busId");
            String operatorIdStr = request.get("operatorId");
             amountStr = request.get("amount");

            if (busIdStr == null || operatorIdStr == null || amountStr == null) {
                logger.warn("Missing required fields in request");
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "message", "busId, operatorId, and amount are required"));
            }

            UUID busId = UUID.fromString(busIdStr);
            UUID operatorId = UUID.fromString(operatorIdStr);
            BigDecimal amount = new BigDecimal(amountStr);

            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                logger.warn("Invalid amount: {}", amount);
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "message", "Amount must be greater than zero"));
            }

            // Fetch passenger
            passenger = userService.findUserByEmail(passengerEmail)
                    .orElseThrow(() -> {
                        logger.warn("Passenger not found: {}", passengerEmail);
                        return new RuntimeException("Passenger not found");
                    });

            // Fetch passenger wallet
            Wallet passengerWallet = walletRepository.findByUserAndType(passenger, Wallet.WalletType.PASSENGER)
                    .orElseThrow(() -> {
                        logger.warn("Passenger wallet not found for email: {}", passengerEmail);
                        return new RuntimeException("Passenger wallet not found");
                    });

            // Check sufficient balance
            if (passengerWallet.getBalance().compareTo(amount) < 0) {
                logger.warn("Insufficient balance for passenger: {}, balance: {}, amount: {}",
                        passengerEmail, passengerWallet.getBalance(), amount);
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "message", "Insufficient balance"));
            }

            // Fetch bus
            Bus bus = busRepository.findById(busId)
                    .orElseThrow(() -> {
                        logger.warn("Bus not found: {}", busId);
                        return new RuntimeException("Bus not found");
                    });

            // Fetch operator
            User operator = userService.findUserById(operatorId)
                    .orElseThrow(() -> {
                        logger.warn("Operator not found: {}", operatorId);
                        return new RuntimeException("Operator not found");
                    });

            if (!operator.getRole().equals(User.UserRole.OPERATOR)) {
                logger.warn("User is not an operator: {}", operatorId);
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "message", "Invalid operator"));
            }

            // Fetch operator assignment to get owner (must be ACTIVE)
            OperatorAssignment assignment = operatorAssignmentRepository
                    .findByOperatorAndBusAndStatus(operator, bus, OperatorAssignment.AssignmentStatus.ACTIVE)
                    .orElseThrow(() -> {
                        logger.warn("Operator {} not assigned to bus {} with ACTIVE status", operatorId, busId);
                        return new RuntimeException("Operator not assigned to bus or not ACTIVE");
                    });

            User owner = assignment.getBus().getOwner();
            if (!owner.getRole().equals(User.UserRole.OWNER)) {
                logger.warn("User is not an owner: {}", owner.getId());
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "message", "Invalid owner"));
            }

            // Fetch bus wallet (instead of owner wallet)
            Wallet busWallet = walletRepository.findByBusAndType(bus, Wallet.WalletType.BUS)
                    .orElseThrow(() -> {
                        logger.warn("Bus wallet not found for bus: {}", bus.getId());
                        return new RuntimeException("Bus wallet not found");
                    });

            // Update wallets: subtract from passenger, add to bus wallet
            passengerWallet.setBalance(passengerWallet.getBalance().subtract(amount));
            busWallet.setBalance(busWallet.getBalance().add(amount));
            walletRepository.save(passengerWallet);
            walletRepository.save(busWallet);

            // Create transaction (to_wallet is busWallet, owner is still set)
            Transaction transaction = new Transaction(amount, Transaction.TransactionType.PAYMENT, passenger, bus, operator);
            transaction.setOwner(owner);
            transaction.setFromWallet(passengerWallet);
            transaction.setToWallet(busWallet);
            transaction.setStatus(Transaction.TransactionStatus.APPROVED);
            transaction.setNote("Payment for bus " + bus.getBusNumber());
            transactionRepository.save(transaction);

            // Publish MQTT notification to operator
            String topic = "bus/" + busId + "/payment";

            // newly added
            String message = String.format(
                    "{\"transactionId\": \"%s\", \"busId\": \"%s\", \"passengerId\": \"%s\", \"passengerName\": \"%s\", \"operatorId\": \"%s\", \"amount\": %s, \"busNumber\": \"%s\", \"status\": \"%s\"}",
                    transaction.getId(), busId, passenger.getId(), passenger.getName(), operatorId, amount, bus.getBusNumber(), transaction.getStatus()
            );
            // Print to command line for debugging
            System.out.println("MQTT DEBUG | Topic: " + topic + " | Message: " + message);
            try {
                MqttMessage mqttMessage = new MqttMessage(message.getBytes());
                mqttMessage.setQos(1); // At least once delivery
                mqttClient.publish(topic, mqttMessage);
                logger.info("Published MQTT message to topic {}: {}", topic, message);
            } catch (MqttException e) {
                logger.error("Failed to publish MQTT message: {}", e.getMessage(), e);
                // Continue processing even if MQTT fails to avoid blocking payment
            }

            logger.info("Payment processed: passenger={}, bus={}, operator={}, owner={}, amount={}",
                    passengerEmail, busId, operatorId, owner.getId(), amount);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Payment processed successfully",
                    "data", Map.of(
                            "transactionId", transaction.getId(),
                            "amount", amount,
                            "busNumber", bus.getBusNumber(),
                            "operatorName", operator.getName(),
                            "ownerEmail", owner.getEmail()
                    )
            ));

        } catch (IllegalArgumentException e) {
            logger.warn("Invalid input: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", e.getMessage()));
        } catch (RuntimeException e) {
            logger.error("Payment processing failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Payment processing failed: " + e.getMessage()));
        }
    }

    @PostMapping("/owner/{ownerId}/withdraw")
    public ResponseEntity<?> withdraw(@PathVariable String ownerId, @RequestBody OwnerWithdrawRequestDto request) {
        UUID ownerUuid = UUID.fromString(ownerId);
        logger.info("Received withdraw request for owner: {}", ownerId);
        try {
            WithdrawalTransactionDto wtd = ownerWithdrawService.handleWithdraw(ownerUuid, request);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Withdrawal request processed successfully",
                "data", wtd
            ));
        } catch (IllegalArgumentException e) {
            logger.warn("Withdraw failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        } catch (Exception e) {
            logger.error("Withdraw failed: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Withdrawal failed: " + e.getMessage()
            ));
        }
    }

    private static String computeHmacSha256Base64(String secret, String message) throws Exception {
        javax.crypto.Mac sha256_HMAC = javax.crypto.Mac.getInstance("HmacSHA256");
        javax.crypto.spec.SecretKeySpec secret_key = new javax.crypto.spec.SecretKeySpec(secret.getBytes(java.nio.charset.StandardCharsets.UTF_8), "HmacSHA256");
        sha256_HMAC.init(secret_key);
        byte[] hash = sha256_HMAC.doFinal(message.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        return java.util.Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
    }

    private String getHmacSecretForPassenger(String email) {
        return passengerHmacSecret; // common key used by all passenger devices

    }
}
