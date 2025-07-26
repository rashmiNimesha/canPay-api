 package com.canpay.api.controller.account;

import com.canpay.api.entity.*;
import com.canpay.api.repository.dashboard.DWalletRepository;
import com.canpay.api.service.implementation.JwtService;
import com.canpay.api.repository.BusRepository;
import com.canpay.api.repository.OperatorAssignmentRepository;
import com.canpay.api.repository.TransactionRepository;
import com.canpay.api.service.implementation.UserServiceImpl;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final JwtService jwtService;
    private final UserServiceImpl userService;
    private final BusRepository busRepository;
    private final OperatorAssignmentRepository operatorAssignmentRepository;
    private final DWalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final MqttClient mqttClient;
    private final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    public PaymentController(JwtService jwtService, UserServiceImpl userService, BusRepository busRepository,
                             OperatorAssignmentRepository operatorAssignmentRepository, DWalletRepository walletRepository,
                             TransactionRepository transactionRepository, MqttClient mqttClient) {
        this.jwtService = jwtService;
        this.userService = userService;
        this.busRepository = busRepository;
        this.operatorAssignmentRepository = operatorAssignmentRepository;
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
        this.mqttClient = mqttClient;
    }

    @PostMapping("/process")
    @PreAuthorize("hasRole('PASSENGER')")
    @Transactional
    public ResponseEntity<?> processPayment(@RequestHeader(value = "Authorization") String authHeader,
                                            @RequestBody Map<String, String> request) {
        logger.debug("Received payment request: {}", request);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.warn("Authorization header missing or invalid");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "Authorization header with Bearer token is required"));
        }

        String token = authHeader.substring(7);
        String passengerEmail;
        try {
            passengerEmail = jwtService.extractEmail(token);
            String role = jwtService.extractRole(token);
            if (!"PASSENGER".equals(role)) {
                logger.warn("Invalid role: {}", role);
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("success", false, "message", "Invalid role for payment"));
            }
        } catch (Exception e) {
            logger.warn("Invalid token: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "Invalid or expired token"));
        }

        try {
            // Validate request
            String busIdStr = request.get("busId");
            String operatorIdStr = request.get("operatorId");
            String amountStr = request.get("amount");

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
            User passenger = userService.findUserByEmail(passengerEmail)
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

            // Fetch operator assignment to get owner
            OperatorAssignment assignment = operatorAssignmentRepository.findByOperatorAndBus(operator, bus)
                    .orElseThrow(() -> {
                        logger.warn("Operator {} not assigned to bus {}", operatorId, busId);
                        return new RuntimeException("Operator not assigned to bus");
                    });

            User owner = assignment.getBus().getOwner();
            if (!owner.getRole().equals(User.UserRole.OWNER)) {
                logger.warn("User is not an owner: {}", owner.getId());
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "message", "Invalid owner"));
            }

            // Fetch owner wallet
            Wallet ownerWallet = walletRepository.findByUserAndType(owner, Wallet.WalletType.OWNER)
                    .orElseThrow(() -> {
                        logger.warn("Owner wallet not found for email: {}", owner.getEmail());
                        return new RuntimeException("Owner wallet not found");
                    });

            // Update wallets
            passengerWallet.setBalance(passengerWallet.getBalance().subtract(amount));
            ownerWallet.setBalance(ownerWallet.getBalance().add(amount));
            walletRepository.save(passengerWallet);
            walletRepository.save(ownerWallet);

            // Create transaction
            Transaction transaction = new Transaction(amount, Transaction.TransactionType.PAYMENT, passenger, bus, operator);
            transaction.setOwner(owner);
            transaction.setFromWallet(passengerWallet);
            transaction.setToWallet(ownerWallet);
            transaction.setStatus(Transaction.TransactionStatus.APPROVED);
            transaction.setNote("Payment for bus " + bus.getBusNumber());
            transactionRepository.save(transaction);

            // Publish MQTT notification to operator
            String topic = "bus/" + busId + "/payment";
            String message = String.format(
                    "{\"transactionId\": \"%s\", \"busId\": \"%s\", \"passengerId\": \"%s\", \"operatorId\": \"%s\", \"amount\": %s, \"busNumber\": \"%s\", \"status\": \"%s\"}",
                    transaction.getId(), busId, passenger.getId(), operatorId, amount, bus.getBusNumber(), transaction.getStatus()
            );
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
}
