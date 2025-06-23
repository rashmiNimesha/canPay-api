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

        boolean valid = otpService.verifyOtp(email, otp);
        System.out.println("OTP verified");
        if (!valid) return ResponseEntity.status(401).body(Map.of("message", "Invalid OTP"));

        Optional<User> existingUser = userServiceImpl.findByEmail(email);
        if (existingUser.isPresent()) {
            String token = jwtService.generateToken(existingUser.get());
            return ResponseEntity.ok(Map.of(
                    "newUser", false,
                    "token", token,
                    "profile", existingUser.get()
            ));
        }

        System.out.println("going to create new acc");

        userServiceImpl.registerWithEmail(email);
        System.out.println("create new acc not existing in verify otp");

        return ResponseEntity.ok(Map.of("newUser", true, "message", "OTP verified and email registered"));
    }



    @PostMapping("/create-profile")
    public ResponseEntity<?> createProfile(@RequestBody Map<String, String> request) {
        System.out.println("came create acc");

        String email = request.get("email");
        String name = request.get("name");
        String nic = request.get("nic");
        String accNoStr = request.get("accNo");
        String bank = request.get("bank");
        String accName = request.get("accName");

        if (email == null || name == null || nic == null || accNoStr == null || bank == null || accName == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Missing required fields"));
        }

        long accNo;
        try {
            accNo = Long.parseLong(accNoStr);
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid account number"));
        }

        try {
            User user = userServiceImpl.updateProfileWithBankAccount(email, name, nic, accName, bank, accNo);
            System.out.println("profile updated");

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
