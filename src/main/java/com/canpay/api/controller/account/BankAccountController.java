package com.canpay.api.controller.account;

import com.canpay.api.dto.AddBankAccountRequest;
import com.canpay.api.dto.BankAccountDto;
import com.canpay.api.entity.BankAccount;
import com.canpay.api.entity.User;
import com.canpay.api.repository.UserRepository;
import com.canpay.api.service.implementation.BankAccountServiceImpl;
import com.canpay.api.service.implementation.JwtService;
import com.canpay.api.service.implementation.UserServiceImpl;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/bank-account")
public class BankAccountController {

    private static final Logger logger = LoggerFactory.getLogger(BankAccountController.class);
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final BankAccountServiceImpl bankAccountService;
    private final UserServiceImpl userServiceImpl;

    public BankAccountController(UserRepository userRepository, JwtService jwtService, BankAccountServiceImpl bankAccountService, UserServiceImpl userServiceImpl) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.bankAccountService = bankAccountService;
        this.userServiceImpl = userServiceImpl;
    }

    @GetMapping("/by-email")
    public ResponseEntity<?> getBankAccountsByEmail(Authentication authentication) {
        String email = authentication.getName(); // Extracted from JWT
        String role = authentication.getAuthorities().iterator().next().getAuthority(); // ROLE_PASSENGER

        // Convert ROLE_PASSENGER to PASSENGER
        String plainRole = role.replace("ROLE_", "");
        Optional<User> userOpt = userRepository.findByEmailAndRole(
                email,
                User.UserRole.valueOf(plainRole)
        );

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(404).body("User not found");
        }

        User user = userOpt.get();
        List<BankAccountDto> result = user.getBankAccounts().stream()
                .map(acc -> new BankAccountDto(
                        acc.getBankName(),
                        acc.getAccountNumber(),
                        acc.getAccountName()
                ))
                .toList();

        System.out.println("Loaded bank accounts for " + email);
        return ResponseEntity.ok(result);
    }


    @GetMapping("/list")
    @PreAuthorize("hasRole('PASSENGER')")
    public ResponseEntity<?> getPassengerBankAccounts(
            @RequestHeader(value = "Authorization") String authHeader) {
        logger.debug("Received passenger bank accounts request");

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
                        .body(Map.of("success", false, "message", "Invalid role for passenger bank accounts"));
            }
        } catch (Exception e) {
            logger.warn("Invalid token: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "Invalid or expired token"));
        }

        try {
            User user = bankAccountService.getUserByEmailAndRole(email);
            if (user == null) {
                logger.warn("User not found for email: {}", email);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("success", false, "message", "User not found"));
            }

            List<BankAccountDto> accounts = bankAccountService.getAccountsByEmail(email);
            logger.info("Fetched {} bank accounts for email: {}", accounts.size(), email);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Successfully retrieved bank accounts",
                    "data", accounts
            ));

        } catch (RuntimeException e) {
            logger.error("Failed to fetch bank accounts for email: {}. Reason: {}", email, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Failed to fetch bank accounts: " + e.getMessage()));
        }
    }



    @PostMapping("/add")
    @PreAuthorize("hasRole('PASSENGER')")
    public ResponseEntity<?> addBankAccount(
            @RequestHeader(value = "Authorization") String authHeader,
            @Valid @RequestBody AddBankAccountRequest request) {
        logger.debug("Received request to add bank account");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.warn("Authorization header missing or invalid");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "Authorization header with Bearer token is required"));
        }

        String token = authHeader.substring(7);
        String email;
        try {
            email = jwtService.extractEmail(token);
        } catch (Exception e) {
            logger.warn("Invalid token: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "Invalid or expired token"));
        }

        try {
            BankAccount bankAccount = userServiceImpl.addBankAccount(
                    email,
                    request.getAccountName(),
                    request.getBankName(),
                    request.getAccountNumber(),
                    request.isDefault()
            );

            Map<String, Object> data = new HashMap<>();
            data.put("id", bankAccount.getId());
            data.put("bankName", bankAccount.getBankName());
            data.put("accountNumber", bankAccount.getAccountNumber());
            data.put("accountName", bankAccount.getAccountName());
            data.put("isDefault", bankAccount.isDefault());

            logger.info("Bank account added successfully for user: {}", email);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Bank account added successfully",
                    "data", data
            ));
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid bank account details: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", e.getMessage()));
        } catch (RuntimeException e) {
            logger.error("Error adding bank account: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false, "message", e.getMessage()));
        } catch (Exception e) {
            logger.error("Unexpected error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Error adding bank account"));
        }
    }
}
