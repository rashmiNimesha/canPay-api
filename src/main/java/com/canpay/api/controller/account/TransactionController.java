package com.canpay.api.controller.account;

import com.canpay.api.dto.dashboard.transactions.RechargeTransactionDto;
import com.canpay.api.entity.ResponseEntityBuilder;
import com.canpay.api.entity.Transaction;
import com.canpay.api.service.dashboard.DTransactionService;
import com.canpay.api.service.implementation.JwtService;
import com.canpay.api.service.implementation.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionController {
    private final TransactionService transactionService;
    private final JwtService jwtService;
    private final DTransactionService dTransactionService;
    private final Logger logger = LoggerFactory.getLogger(TransactionController.class);

    public TransactionController(TransactionService transactionService, JwtService jwtService, DTransactionService dTransactionService) {
        this.transactionService = transactionService;
        this.jwtService = jwtService;
        this.dTransactionService = dTransactionService;
    }

    @GetMapping("/recent/{passengerId}")
    @PreAuthorize("hasRole('PASSENGER')")
    public ResponseEntity<?> getRecentTransactions(@RequestHeader(value = "Authorization") String authHeader, @PathVariable String passengerId) {
        logger.debug("Received request for recent transactions");
        UUID passengerUuid;
        try {
            passengerUuid = UUID.fromString(passengerId);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid UUID format for busId: {}", passengerId);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "message", "Invalid bus ID format: " + passengerId));
        }


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
                        .body(Map.of("success", false, "message", "Invalid role for accessing transactions"));
            }
        } catch (Exception e) {
            logger.warn("Invalid token: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "Invalid or expired token"));
        }

        try {
            List<Transaction> transactions = transactionService.getRecentTransactions(passengerUuid);
            List<Map<String, Object>> transactionData = transactions.stream().map(t -> {
                Map<String, Object> data = new HashMap<>();
                data.put("transactionId", t.getId().toString());
                data.put("amount", t.getAmount());
                data.put("happenedAt", t.getHappenedAt().toString());
                data.put("type", t.getType().toString());
                data.put("status", t.getStatus().toString());
                data.put("note", t.getNote());
                data.put("busNumber", t.getBus() != null ? t.getBus().getBusNumber() : null);
                data.put("operatorName", t.getOperator() != null ? t.getOperator().getName() : null);
                data.put("ownerEmail", t.getOwner() != null ? t.getOwner().getEmail() : null);
                return data;
            }).toList();

            logger.info("Returning {} recent transactions for passenger: {}", transactionData.size(), passengerEmail);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", transactionData));
        } catch (RuntimeException e) {
            logger.error("Error fetching transactions: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Error fetching transactions: " + e.getMessage()));
        }
    }

    @GetMapping("/passenger/{passengerId}")
    @PreAuthorize("hasRole('PASSENGER')")
    public ResponseEntity<?> getRechargeTransactionsByPassengerId(@PathVariable UUID passengerId) {
        System.out.println("metha nat awa");
        List<RechargeTransactionDto> transactions = transactionService
                .getRechargeTransactionsByPassengerId(passengerId);
        return new ResponseEntityBuilder.Builder<List<RechargeTransactionDto>>()
                .resultMessage("Recharge transactions by passenger retrieved successfully")
                .httpStatus(HttpStatus.OK)
                .body(transactions)
                .buildWrapped();
    }

    @GetMapping("/owner/{ownerId}/all")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<?> getAllOwnerTransactions(@RequestHeader(value = "Authorization") String authHeader, @PathVariable UUID ownerId) {
        // Validate JWT and role
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "Authorization header with Bearer token is required"));
        }
        String token = authHeader.substring(7);
        try {
            String role = jwtService.extractRole(token);
            if (!"OWNER".equals(role)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("success", false, "message", "Invalid role for accessing owner transactions"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "Invalid or expired token"));
        }

        // Get transactions
        Map<String, Object> data = dTransactionService.getAllOwnerTransactions(ownerId);
        return new ResponseEntityBuilder.Builder<Map<String, Object>>()
                .resultMessage("Owner transactions retrieved successfully")
                .httpStatus(HttpStatus.OK)
                .body(data)
                .buildWrapped();
    }

}