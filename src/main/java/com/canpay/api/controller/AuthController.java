package com.canpay.api.controller;

import com.canpay.api.dto.UserDto;
import com.canpay.api.entity.User;
import com.canpay.api.service.implementation.JwtService;
import com.canpay.api.service.implementation.OTPService;
import com.canpay.api.service.implementation.UserServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserServiceImpl userServiceImpl;
    private final OTPService otpService;
    private final JwtService jwtService;

    public AuthController(OTPService otpService, UserServiceImpl userServiceImpl, JwtService jwtService) {
        this.otpService = otpService;
        this.userServiceImpl = userServiceImpl;
        this.jwtService = jwtService;
    }

    @PostMapping("/send-otp")
    public ResponseEntity<?> sendOtp(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        otpService.sendOtp(email);
        return ResponseEntity.ok(Map.of("message", "OTP sent successfully"));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String otp = request.get("otp");
        String role = request.getOrDefault("role", "PASSENGER").toUpperCase();

        if (email == null || otp == null || role.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Missing required fields"));
        }

        boolean valid = otpService.verifyOtp(email, otp);
        System.out.println("OTP verified");

        if (!valid) {
            return ResponseEntity.status(401).body(Map.of("message", "Invalid OTP"));
        }

        Optional<User> existingUser = userServiceImpl.findByEmail(email);

        if (existingUser.isPresent()) {
            String token = jwtService.generateToken(existingUser.get());
            return ResponseEntity.ok(Map.of(
                    "newUser", false,
                    "token", token,
                    "profile", new UserDto(existingUser.get())

            ));
        }

        System.out.println("Going to create new account with role: " + role);
        userServiceImpl.registerWithEmail(email, role);
        System.out.println("New account created after OTP verification");

        return ResponseEntity.ok(Map.of(
                "newUser", true,
                "message", "OTP verified and email registered"
        ));
    }


    @PostMapping("/create-profile")
    public ResponseEntity<?> createProfile(@RequestBody Map<String, String> request) {
        System.out.println("came create acc");

        String email = request.get("email");
        String name = request.get("name");
        String nic = request.get("nic");
        String role = request.getOrDefault("role", "PASSENGER").toUpperCase();


        if (email == null || name == null || nic == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Missing name/email/nic"));
        }

        try {
            User user;
            switch (role) {
                case "PASSENGER":
                    String accNoStrP = request.get("accNo");
                    String bankP = request.get("bank");
                    String accNameP = request.get("accName");

                    if (accNoStrP == null || bankP == null || accNameP == null)
                        return ResponseEntity.badRequest().body(Map.of("message", "Missing bank details"));

                    long accNoP;
                    try {
                        accNoP = Long.parseLong(accNoStrP);
                    } catch (NumberFormatException e) {
                        return ResponseEntity.badRequest().body(Map.of("message", "Invalid account number"));
                    }

                    user = userServiceImpl.updateProfileWithBankAccount(email, name, nic, accNameP, bankP, accNoP);
                    break;

                case "OPERATOR":
                    String profileImageOp = request.get("profileImage");
                    if (profileImageOp == null)
                        return ResponseEntity.badRequest().body(Map.of("message", "Missing profile image"));

                    user = userServiceImpl.updateOperatorProfile(email, name, nic, profileImageOp);
                    break;

                case "OWNER":
                    String profileImageOw = request.get("profileImage");
                    String accNoStrOw = request.get("accNo");
                    String bankOw = request.get("bank");
                    String accNameOw = request.get("accName");

                    if (profileImageOw == null || accNoStrOw == null || bankOw == null || accNameOw == null)
                        return ResponseEntity.badRequest().body(Map.of("message", "Missing owner data"));

                    long accNoOw;
                    try {
                        accNoOw = Long.parseLong(accNoStrOw);
                    } catch (NumberFormatException e) {
                        return ResponseEntity.badRequest().body(Map.of("message", "Invalid account number"));
                    }

                    user = userServiceImpl.updateOwnerProfile(email, name, nic, profileImageOw, accNameOw, bankOw, accNoOw);
                    break;

                default:
                    return ResponseEntity.badRequest().body(Map.of("message", "Invalid role"));
            }

            String token = jwtService.generateToken(user);
            UserDto userDto = new UserDto(user.getName(), user.getEmail(), user.getNic());

            return ResponseEntity.ok(Map.of(
                    "message", "Profile updated successfully",
                    "token", token,
                    "profile", userDto
            ));

        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(Map.of("message", e.getMessage()));
        }
    }
}
