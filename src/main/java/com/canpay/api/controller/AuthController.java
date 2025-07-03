package com.canpay.api.controller;

import com.canpay.api.dto.UserDto;
import com.canpay.api.entity.ResponseEntityBuilder;
import com.canpay.api.entity.User;
import com.canpay.api.entity.User.UserRole;
import com.canpay.api.service.implementation.JwtService;
import com.canpay.api.service.implementation.OTPService;
import com.canpay.api.service.implementation.UserServiceImpl;
import io.jsonwebtoken.JwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserServiceImpl userServiceImpl;
    private final OTPService otpService;
    private final JwtService jwtService;
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    public AuthController(OTPService otpService, UserServiceImpl userServiceImpl, JwtService jwtService) {
        this.otpService = otpService;
        this.userServiceImpl = userServiceImpl;
        this.jwtService = jwtService;
    }

//    @PostMapping("/send-otp")
//    public ResponseEntity<?> sendOtp(@RequestBody Map<String, String> request) {
//        String email = request.get("email");
//        if (email == null || email.isBlank()) {
//            return new ResponseEntityBuilder.Builder<Void>()
//                    .resultMessage("Email is required")
//                    .httpStatus(HttpStatus.BAD_REQUEST)
//                    .buildWrapped();
//        }
//
//        otpService.sendOtp(email);
//        return new ResponseEntityBuilder.Builder<Void>()
//                .resultMessage("OTP sent successfully")
//                .httpStatus(HttpStatus.OK)
//                .buildWrapped();
//    }

    @PostMapping("/send-otp")
    public ResponseEntity<?> sendOtp(@RequestBody Map<String, String> request) {
        logger.debug("Received send-otp request: {}", request);
        String email = request.get("email");
        if (email == null || email.isBlank()) {
            logger.warn("Email is required in send-otp request");
            return new ResponseEntityBuilder.Builder<Void>()
                    .resultMessage("Email is required")
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .buildWrapped();
        }

        try {
            otpService.sendOtp(email);
            logger.info("OTP sent successfully for email: {}", email);
            return new ResponseEntityBuilder.Builder<Void>()
                    .resultMessage("OTP sent successfully")
                    .httpStatus(HttpStatus.OK)
                    .buildWrapped();
        } catch (Exception e) {
            logger.error("Failed to send OTP for email: {}", email, e);
            return new ResponseEntityBuilder.Builder<Void>()
                    .resultMessage("Failed to send OTP: " + e.getMessage())
                    .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                    .buildWrapped();
        }
    }


    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody Map<String, String> request) {
        logger.debug("Received verify-otp request: {}", request);
        String email = request.get("email");
        String otp = request.get("otp");
        String roleStr = request.getOrDefault("role", "").toUpperCase();
        UserRole role;
        try {
            role = UserRole.valueOf(roleStr);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid role provided: {}", roleStr);
            return new ResponseEntityBuilder.Builder<Map<String, Object>>()
                    .resultMessage("Invalid role")
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .buildWrapped();
        }

        if (email == null || otp == null || roleStr.isBlank()) {
            logger.warn("Missing required fields in verify-otp request: email={}, role={}", email, roleStr);
            return new ResponseEntityBuilder.Builder<Map<String, Object>>()
                    .resultMessage("Missing required fields")
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .buildWrapped();
        }

        boolean valid = otpService.verifyOtp(email, otp);
        if (!valid) {
            logger.warn("Invalid OTP for email: {}", email);
            return new ResponseEntityBuilder.Builder<Map<String, Object>>()
                    .resultMessage("Invalid OTP")
                    .httpStatus(HttpStatus.UNAUTHORIZED)
                    .buildWrapped();
        }

        Optional<User> existingUser = userServiceImpl.findByEmailAndRole(email, role);
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            String token = jwtService.generateToken(user);
            logger.info("Login successful for email: {} and role: {}", email, role);
            Map<String, Object> responseData = Map.of(
                    "newUser", false,
                    "token", token,
                    "profile", new UserDto(user)
            );
            return new ResponseEntityBuilder.Builder<Map<String, Object>>()
                    .resultMessage("Login successful")
                    .httpStatus(HttpStatus.OK)
                    .body(responseData)
                    .buildWrapped();
        }

        try {
            User newUser = userServiceImpl.registerWithEmail(email, roleStr);
            logger.info("New role registered for email: {} and role: {}", email, roleStr);
            String token = jwtService.generateToken(newUser); // Generate token for new user

            Map<String, Object> responseData = Map.of(
                    "newUser", true,
                    "token", token,
                    "profile", new UserDto(newUser)
            );
            return new ResponseEntityBuilder.Builder<Map<String, Object>>()
                    .resultMessage("OTP verified and new role registered")
                    .httpStatus(HttpStatus.OK)
                    .body(responseData)
                    .buildWrapped();
        } catch (IllegalArgumentException e) {
            logger.warn("Registration failed for email: {} and role: {}. Reason: {}", email, roleStr, e.getMessage());
            return new ResponseEntityBuilder.Builder<Map<String, Object>>()
                    .resultMessage(e.getMessage())
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .buildWrapped();
        }
    }


    @PostMapping("/create-profile")
    public ResponseEntity<?> createProfile(
            @RequestHeader(value = "Authorization") String authHeader,
            @RequestBody Map<String, String> request) {
        logger.debug("Received create-profile request: {}", request);
        String email = request.get("email");
        String name = request.get("name");
        String nic = request.get("nic");

        // Require Authorization header with Bearer token
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.warn("Authorization header is missing or does not start with Bearer");
            return new ResponseEntityBuilder.Builder<Map<String, Object>>()
                    .resultMessage("Authorization header with Bearer token is required")
                    .httpStatus(HttpStatus.UNAUTHORIZED)
                    .buildWrapped();
        }

        // Extract role from JWT token
        UserRole role;
        String token = authHeader.substring(7);
        logger.debug("Received token: {}", token);
        try {
            String tokenRole = jwtService.extractRole(token);
            role = UserRole.valueOf(tokenRole);
            logger.debug("Role extracted from JWT token: {}", role);
        } catch (JwtException e) {
            logger.warn("JWT token validation failed: {}", e.getMessage());
            return new ResponseEntityBuilder.Builder<Map<String, Object>>()
                    .resultMessage("Invalid or expired token: " + e.getMessage())
                    .httpStatus(HttpStatus.UNAUTHORIZED)
                    .buildWrapped();
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid role in JWT token: {}", e.getMessage());
            return new ResponseEntityBuilder.Builder<Map<String, Object>>()
                    .resultMessage("Invalid role in token")
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .buildWrapped();
        }

        if (email == null || name == null || nic == null) {
            logger.warn("Missing required fields in create-profile request: email={}, name={}, nic={}", email, name, nic);
            return new ResponseEntityBuilder.Builder<Map<String, Object>>()
                    .resultMessage("Missing required fields")
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .buildWrapped();
        }

        // Validate email matches the token's subject
        try {
            String tokenEmail = jwtService.extractEmail(token);
            if (!email.equals(tokenEmail)) {
                logger.warn("Email in request ({}) does not match token email ({})", email, tokenEmail);
                return new ResponseEntityBuilder.Builder<Map<String, Object>>()
                        .resultMessage("Email does not match token")
                        .httpStatus(HttpStatus.BAD_REQUEST)
                        .buildWrapped();
            }
        } catch (JwtException e) {
            logger.warn("JWT token validation failed: {}", e.getMessage());
            return new ResponseEntityBuilder.Builder<Map<String, Object>>()
                    .resultMessage("Invalid or expired token: " + e.getMessage())
                    .httpStatus(HttpStatus.UNAUTHORIZED)
                    .buildWrapped();
        }

        try {
            User user;
            switch (role) {
                case PASSENGER:
                    String accNoStrP = request.get("accNo");
                    String bankP = request.get("bank");
                    String accNameP = request.get("accName");

                    if (accNoStrP == null || bankP == null || accNameP == null) {
                        logger.warn("Missing bank details for PASSENGER profile: accNo={}, bank={}, accName={}", accNoStrP, bankP, accNameP);
                        return new ResponseEntityBuilder.Builder<Map<String, Object>>()
                                .resultMessage("Missing bank details")
                                .httpStatus(HttpStatus.BAD_REQUEST)
                                .buildWrapped();
                    }

                    long accNoP;
                    try {
                        accNoP = Long.parseLong(accNoStrP);
                    } catch (NumberFormatException e) {
                        logger.warn("Invalid account number for PASSENGER profile: {}", accNoStrP);
                        return new ResponseEntityBuilder.Builder<Map<String, Object>>()
                                .resultMessage("Invalid account number")
                                .httpStatus(HttpStatus.BAD_REQUEST)
                                .buildWrapped();
                    }

                    logger.debug("Attempting to update PASSENGER profile for email: {}, name: {}, nic: {}, accName: {}, bank: {}, accNo: {}",
                            email, name, nic, accNameP, bankP, accNoP);
                    user = userServiceImpl.updatePassengerProfile(email, name, nic, accNameP, bankP, accNoP, role);
                    if (user == null) {
                        logger.error("updateProfileWithBankAccount returned null for email: {} and role: {}", email, role);
                        return new ResponseEntityBuilder.Builder<Map<String, Object>>()
                                .resultMessage("Failed to update profile: User not found or invalid data")
                                .httpStatus(HttpStatus.NOT_FOUND)
                                .buildWrapped();
                    }
                    break;

                case OPERATOR:
                    String profileImageOp = request.get("profileImage");
                    if (profileImageOp == null) {
                        logger.warn("Missing profile image for OPERATOR profile");
                        return new ResponseEntityBuilder.Builder<Map<String, Object>>()
                                .resultMessage("Missing profile image")
                                .httpStatus(HttpStatus.BAD_REQUEST)
                                .buildWrapped();
                    }
                    logger.debug("Attempting to update OPERATOR profile for email: {}, name: {}, nic: {}, profileImage: {}",
                            email, name, nic, profileImageOp);
                    user = userServiceImpl.updateOperatorProfile(email, name, nic, profileImageOp, role);
                    if (user == null) {
                        logger.error("updateOperatorProfile returned null for email: {} and role: {}", email, role);
                        return new ResponseEntityBuilder.Builder<Map<String, Object>>()
                                .resultMessage("Failed to update profile: User not found or invalid data")
                                .httpStatus(HttpStatus.NOT_FOUND)
                                .buildWrapped();
                    }
                    break;

                case OWNER:
                    String profileImageOw = request.get("profileImage");
                    String accNoStrOw = request.get("accNo");
                    String bankOw = request.get("bank");
                    String accNameOw = request.get("accName");

                    if (profileImageOw == null || accNoStrOw == null || bankOw == null || accNameOw == null) {
                        logger.warn("Missing owner data: profileImage={}, accNo={}, bank={}, accName={}", profileImageOw, accNoStrOw, bankOw, accNameOw);
                        return new ResponseEntityBuilder.Builder<Map<String, Object>>()
                                .resultMessage("Missing owner data")
                                .httpStatus(HttpStatus.BAD_REQUEST)
                                .buildWrapped();
                    }
                    long accNoOw;
                    try {
                        accNoOw = Long.parseLong(accNoStrOw);
                    } catch (NumberFormatException e) {
                        logger.warn("Invalid account number for OWNER profile: {}", accNoStrOw);
                        return new ResponseEntityBuilder.Builder<Map<String, Object>>()
                                .resultMessage("Invalid account number")
                                .httpStatus(HttpStatus.BAD_REQUEST)
                                .buildWrapped();
                    }

                    logger.debug("Attempting to update OWNER profile for email: {}, name: {}, nic: {}, profileImage: {}, accName: {}, bank: {}, accNo: {}",
                            email, name, nic, profileImageOw, accNameOw, bankOw, accNoOw);
                    user = userServiceImpl.updateOwnerProfile(email, name, nic, profileImageOw, accNameOw, bankOw, accNoOw, role);
                    if (user == null) {
                        logger.error("updateOwnerProfile returned null for email: {} and role: {}", email, role);
                        return new ResponseEntityBuilder.Builder<Map<String, Object>>()
                                .resultMessage("Failed to update profile: User not found or invalid data")
                                .httpStatus(HttpStatus.NOT_FOUND)
                                .buildWrapped();
                    }
                    break;

                default:
                    logger.warn("Invalid role: {}", role);
                    return new ResponseEntityBuilder.Builder<Map<String, Object>>()
                            .resultMessage("Invalid role")
                            .httpStatus(HttpStatus.BAD_REQUEST)
                            .buildWrapped();
            }

            token = jwtService.generateToken(user);
            UserDto userDto = new UserDto(user);
            logger.info("Profile updated successfully for email: {} and role: {}", email, role);
            Map<String, Object> responseData = Map.of(
                    "token", token,
                    "profile", userDto
            );
            return new ResponseEntityBuilder.Builder<Map<String, Object>>()
                    .resultMessage("Profile updated successfully")
                    .httpStatus(HttpStatus.OK)
                    .body(responseData)
                    .buildWrapped();

        } catch (RuntimeException e) {
            logger.error("Failed to update profile for email: {} and role: {}. Reason: {}", email, role, e.getMessage(), e);
            return new ResponseEntityBuilder.Builder<Map<String, Object>>()
                    .resultMessage("Failed to update profile: " + (e.getMessage() != null ? e.getMessage() : "Internal error"))
                    .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                    .buildWrapped();
        }
    }


    @PostMapping("/check-user")
    public ResponseEntity<?> checkUser(@RequestBody Map<String, String> request) {
        logger.debug("Received check-user request: {}", request);
        String email = request.get("email");
        String roleStr = request.getOrDefault("role", "").toUpperCase();

        if (email == null || roleStr.isBlank()) {
            logger.warn("Missing email or role in check-user request: email={}, role={}", email, roleStr);
            return new ResponseEntityBuilder.Builder<Map<String, Object>>()
                    .resultMessage("Missing email or role")
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .buildWrapped();
        }

        UserRole role;
        try {
            role = UserRole.valueOf(roleStr);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid role in check-user: {}", roleStr);
            return new ResponseEntityBuilder.Builder<Map<String, Object>>()
                    .resultMessage("Invalid role")
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .buildWrapped();
        }

        boolean exists = userServiceImpl.findByEmailAndRole(email, role).isPresent();
        long roleCount = userServiceImpl.countRolesByEmail(email);
        logger.info("Check user completed for email: {} and role: {}. Exists: {}, CanRegisterNewRole: {}", email, role, exists, roleCount < 3 && !exists);

        return new ResponseEntityBuilder.Builder<Map<String, Object>>()
                .resultMessage("Check completed")
                .httpStatus(HttpStatus.OK)
                .body(Map.of(
                        "exists", exists,
                        "canRegisterNewRole", roleCount < 3 && !exists
                ))
                .buildWrapped();
    }



}
