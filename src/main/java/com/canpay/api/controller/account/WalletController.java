package com.canpay.api.controller.account;

import com.canpay.api.dto.UserWalletBalanceDto;
import com.canpay.api.entity.BankAccount;
import com.canpay.api.entity.ResponseEntityBuilder;
import com.canpay.api.entity.Transaction;
import com.canpay.api.entity.User;
import com.canpay.api.service.implementation.JwtService;
import com.canpay.api.service.implementation.WalletServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/wallet")
public class WalletController {

    private final WalletServiceImpl walletService;
    private static final Logger logger = LoggerFactory.getLogger(WalletController.class);
    private final JwtService jwtService;

    public WalletController(WalletServiceImpl walletService, JwtService jwtService) {
        this.walletService = walletService;
        this.jwtService = jwtService;
    }

    @PostMapping("/recharge")
    @PreAuthorize("hasRole('PASSENGER')")
    public ResponseEntity<?> rechargePassengerWallet(
            @RequestHeader(value = "Authorization") String authHeader,
            @RequestBody Map<String, String> request) {
        logger.debug("Received passenger recharge request: {}", request);

        String email = request.get("email");
        String amountStr = request.get("amount");

        if (email == null || amountStr == null) {
            logger.warn("Missing email or amount in recharge request: email={}, amount={}", email, amountStr);
            return ResponseEntity.badRequest().body(Map.of("message", "Missing email or amount"));
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
            if (amount <= 0) {
                logger.warn("Invalid amount: {}", amountStr);
                return ResponseEntity.badRequest().body(Map.of("message", "Amount must be positive"));
            }
        } catch (NumberFormatException e) {
            logger.warn("Invalid amount format: {}", amountStr);
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid amount format"));
        }

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.warn("Authorization header missing or invalid");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Authorization header with Bearer token is required"));
        }

        String token = authHeader.substring(7);
        String tokenEmail;
        try {
            tokenEmail = jwtService.extractEmail(token);
            String tokenRole = jwtService.extractRole(token);
            if (!"PASSENGER".equals(tokenRole)) {
                logger.warn("Invalid role in token: {}", tokenRole);
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("message", "Invalid role for passenger wallet"));
            }
        } catch (Exception e) {
            logger.warn("Invalid token: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Invalid or expired token"));
        }

        if (!email.equals(tokenEmail)) {
            logger.warn("Email in request ({}) does not match token email ({})", email, tokenEmail);
            return ResponseEntity.badRequest().body(Map.of("message", "Email does not match token"));
        }

        try {
            walletService.rechargePassengerWallet(email, amount);
            double currentBalance = walletService.getPassengerWalletBalance(email);
            UserWalletBalanceDto walletBalanceDto = new UserWalletBalanceDto(email, currentBalance);
            logger.info("Passenger wallet recharged for email: {}, amount: {}, new balance: {}", email, amount, currentBalance);


            Map<String, Object> responseData = Map.of(
                    "newUser", false,
                    "profile", new UserWalletBalanceDto(email, walletBalanceDto.getBalance())
            );

            return new ResponseEntityBuilder.Builder<Map<String, Object>>()
                    .resultMessage("Recharged Passenger Wallet Successfully")
                    .httpStatus(HttpStatus.OK)
                    .body(responseData)
                    .buildWrapped();


        } catch (RuntimeException e) {
            logger.error("Failed to recharge passenger wallet for email: {}. Reason: {}", email, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to recharge passenger wallet: " + e.getMessage()));
        }
    }

//    @GetMapping("/balance")
//    @PreAuthorize("hasRole('PASSENGER')")
//    public ResponseEntity<?> getPassengerWalletBalance(
//            @RequestHeader(value = "Authorization") String authHeader) {
//        logger.debug("Received passenger wallet balance request");
//
//        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//            logger.warn("Authorization header missing or invalid");
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                    .body(Map.of("success", false, "message", "Authorization header with Bearer token is required"));
//        }
//
//        String token = authHeader.substring(7);
//        String email;
//        try {
//            email = jwtService.extractEmail(token);
//            String tokenRole = jwtService.extractRole(token);
//            if (!"PASSENGER".equals(tokenRole)) {
//                logger.warn("Invalid role in token: {}", tokenRole);
//                return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                        .body(Map.of("success", false, "message", "Invalid role for passenger wallet"));
//            }
//        } catch (Exception e) {
//            logger.warn("Invalid token: {}", e.getMessage());
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                    .body(Map.of("success", false, "message", "Invalid or expired token"));
//        }
//
//        try {
//            User user = walletService.getUserByEmailAndRole(email);
//            if (user == null) {
//                logger.warn("User not found for email: {}", email);
//                return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                        .body(Map.of("success", false, "message", "User not found"));
//            }
//
//            double walletBalance = walletService.getPassengerWalletBalance(email);
//            BankAccount defaultBankAccount = user.getBankAccounts() != null ?
//                    user.getBankAccounts().stream()
//                            .filter(BankAccount::isDefault)
//                            .findFirst()
//                            .orElse(null) : null;
//
//            Long accountNumber = defaultBankAccount != null ? defaultBankAccount.getAccountNumber() : null;
//            String accountName = defaultBankAccount != null ? defaultBankAccount.getAccountName() : null;
//
//            UserWalletBalanceDto responseDto = new UserWalletBalanceDto(walletBalance, accountNumber, accountName);
//
//            logger.info("Fetched wallet balance for email: {}, balance: {}, accountNumber: {}, accountName: {}",
//                    email, walletBalance, accountNumber, accountName);
//
//            return ResponseEntity.ok(Map.of(
//                    "success", true,
//                    "message", "Successfully retrieved wallet balance",
//                    "data", responseDto
//            ));
//
//        } catch (RuntimeException e) {
//            logger.error("Failed to fetch wallet balance for email: {}. Reason: {}", email, e.getMessage());
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(Map.of("success", false, "message", "Failed to fetch wallet balance: " + e.getMessage()));
//        }
//    }



    @GetMapping("/balance")
    @PreAuthorize("hasRole('PASSENGER')")
    public ResponseEntity<?> getPassengerWalletBalance(
            @RequestHeader(value = "Authorization") String authHeader) {
        logger.debug("Received passenger wallet balance request");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.warn("Authorization header missing or invalid");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "Authorization header with Bearer token is required"));
        }

        String token = authHeader.substring(7);
        String email;
        try {
            email = jwtService.extractEmail(token);
            String tokenRole = jwtService.extractRole(token);
            if (!"PASSENGER".equals(tokenRole)) {
                logger.warn("Invalid role in token: {}", tokenRole);
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("success", false, "message", "Invalid role for passenger wallet"));
            }
        } catch (Exception e) {
            logger.warn("Invalid token: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "Invalid or expired token"));
        }

        try {
            Map<String, Object> walletDetails = walletService.getPassengerWalletBalanceForDash(email);
            UserWalletBalanceDto responseDto = new UserWalletBalanceDto(
                    (String) walletDetails.get("walletNumber"),
                    (Double) walletDetails.get("balance"),
                    (String) walletDetails.get("name")
            );

            logger.info("Fetched wallet balance for email: {}, walletNumber: {}, balance: {}, name: {}",
                    email, walletDetails.get("walletNumber"), walletDetails.get("balance"), walletDetails.get("name"));

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Successfully retrieved wallet balance",
                    "data", responseDto
            ));
        } catch (RuntimeException e) {
            logger.error("Failed to fetch wallet balance for email: {}. Reason: {}", email, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false, "message", "User not found"));
        } catch (Exception e) {
            logger.error("Unexpected error for email: {}. Reason: {}", email, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Failed to fetch wallet balance: " + e.getMessage()));
        }
    }





    @GetMapping("/history")
    @PreAuthorize("hasRole('PASSENGER') or hasRole('BUS')")
    public ResponseEntity<?> getTransactionHistory(@RequestParam String email) {
        List<Transaction> history = walletService.getTransactionHistory(email);
        return ResponseEntity.ok(history);
    }
}
