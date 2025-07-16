package com.canpay.api.controller.account;

import com.canpay.api.dto.UserDto;
import com.canpay.api.entity.User;
import com.canpay.api.service.implementation.BankAccountServiceImpl;
import com.canpay.api.service.implementation.JwtService;
import com.canpay.api.service.implementation.UserServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/user-service")
public class AccountController {

    public final UserServiceImpl userService;
    private final JwtService jwtService;
    private final BankAccountServiceImpl bankAccountService;
    private final Logger logger = LoggerFactory.getLogger(AccountController.class);

    public AccountController(UserServiceImpl userService, JwtService jwtService,
            BankAccountServiceImpl bankAccountService) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.bankAccountService = bankAccountService;
    }


    @PatchMapping("/passenger-account")
    @PreAuthorize("hasRole('PASSENGER')")
    public ResponseEntity<?> updatePassengerAccount(
            @RequestHeader(value = "Authorization") String authHeader,
            @RequestBody Map<String, String> request) {
        logger.debug("Received passenger account update request: {}", request);

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
                        .body(Map.of("success", false, "message", "Invalid role for passenger account update"));
            }
        } catch (Exception e) {
            logger.warn("Invalid token: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "Invalid or expired token"));
        }

        try {
            User user = userService.findUserByEmail(email)
                    .orElseThrow(() -> {
                        logger.warn("User not found for email: {}", email);
                        return new RuntimeException("User not found for email: " + email);
                    });

            if (!"PASSENGER".equals(user.getRole().toString())) {
                logger.warn("User is not a passenger: {}", email);
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("success", false, "message", "User is not a passenger"));
            }

            boolean updated = false;
            String newToken = "";
            if (request.containsKey("name")) {
                String name = request.get("name");
                user = userService.updateName(email, name);
                updated = true;
            }

            String newEmail = request.get("newemail");
            if (newEmail != null && !newEmail.equalsIgnoreCase(email)) {
                // Check if new email already exists
                Optional<User> existingUser = userService.findUserByEmail(newEmail);
                if (existingUser.isPresent()) {
                    logger.warn("New email already exists in database: {}", newEmail);
                    return ResponseEntity.badRequest()
                            .body(Map.of("success", false, "message", "New email is already registered"));
                }
                user = userService.updateEmail(email, newEmail);
                newToken = jwtService.generateToken(user); // Generate new token for email update
                updated = true;
            }

            if (!updated) {
                logger.warn("No updates provided in request for email: {}", email);
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "message", "No valid update fields provided"));
            }

            UserDto userDto = new UserDto(user.getName(), user.getEmail());
            Map<String, Object> data = Map.of(
                    "profile", userDto,
                    "token", newToken
            );

            logger.info("Passenger account updated for email: {}, newEmail: {}, nameUpdated: {}",
                    email, newEmail != null ? newEmail : "none", request.containsKey("name"));

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Passenger account updated",
                    "data", data
            ));

        } catch (IllegalArgumentException e) {
            logger.warn("Invalid input for email: {}. Reason: {}", email, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", e.getMessage()));
        } catch (RuntimeException e) {
            logger.error("Failed to update passenger account for email: {}. Reason: {}", email, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Failed to update passenger account: " + e.getMessage()));
        }
    }

    @GetMapping("/financial-details")
    @PreAuthorize("hasAnyRole('PASSENGER', 'OWNER')")
    public ResponseEntity<?> getUserFinancialDetails(@RequestHeader(value = "Authorization") String authHeader) {
        logger.debug("Received request for user financial details");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.warn("Authorization header missing or invalid");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "Authorization header with Bearer token is required"));
        }

        String token = authHeader.substring(7);
        String email;
        User.UserRole userRole;
        try {
            email = jwtService.extractEmail(token);
            userRole = User.UserRole.valueOf(jwtService.extractRole(token));
        } catch (Exception e) {
            logger.warn("Invalid token: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "Invalid or expired token"));
        }

        try {
            Map<String, Object> financialDetails = userService.getUserFinancialDetails(email, userRole);
            logger.info("Returning financial details for user: {}", email);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", financialDetails
            ));
        } catch (RuntimeException e) {
            logger.error("Error fetching financial details: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false, "message", e.getMessage()));
        } catch (Exception e) {
            logger.error("Unexpected error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Error fetching financial details"));
        }
    }
}