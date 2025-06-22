package com.canpay.api.controller;

import com.canpay.api.entity.User;
import com.canpay.api.repository.user.UserRepository;
import com.canpay.api.service.implementation.JwtService;
import com.canpay.api.service.implementation.OTPService;
import com.canpay.api.service.implementation.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    UserRepository userRepository;


    @PostMapping("/send-otp")
    public ResponseEntity<?> sendOtp(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        otpService.sendOtp(email);
        return ResponseEntity.ok(Map.of("message", "OTP sent successfully"));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody Map<String, String> request) {
        System.out.println("OTP Verified started");

        String email = request.get("email");
        String otp = request.get("otp");

        boolean valid = otpService.verifyOtp(email, otp);
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
        System.out.println("OTP Verified");

        return ResponseEntity.ok(Map.of("newUser", true, "message", "OTP verified"));
    }


    @PostMapping("/create-profile")
    public ResponseEntity<?> createProfile(@RequestBody Map<String, String> request) {
        String phone = request.get("email");
        String name = request.get("name");

        User newUser = userServiceImpl.createPassenger(phone, name);
        String token = jwtService.generateToken(newUser);

        return ResponseEntity.ok(Map.of(
                "message", "Profile created successfully",
                "token", token,
                "profile", newUser
        ));
    }

    @PostMapping("/set-pin")
    public ResponseEntity<?> setPin(@RequestBody Map<String, String> request) {
        String phone = request.get("email");
        String pin = request.get("pin");
        if (pin.length() < 4) {
            return ResponseEntity.badRequest().body(Map.of("message", "PIN must be at least 4 digits"));
        }

        Optional<User> userOpt = userServiceImpl.setPin(phone, pin);
        if (userOpt.isPresent()) {
            return ResponseEntity.ok(Map.of("message", "PIN set successfully"));
        } else {
            return ResponseEntity.status(404).body(Map.of("message", "User not found"));
        }
    }

    @PostMapping("/confirm-pin")
    public ResponseEntity<?> confirmPin(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String pin = request.get("pin");

        Optional<User> userOpt = userServiceImpl.findByEmail(email);
        if (userOpt.isPresent() && pin.equals(userOpt.get().getPin())) {
            return ResponseEntity.ok(Map.of("message", "PIN confirmed", "status", "success"));
        } else {
            return ResponseEntity.status(401).body(Map.of("message", "Invalid PIN"));
        }
    }

    @PostMapping("/login-with-pin")
    public ResponseEntity<?> loginWithPin(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String pin = request.get("pin");

        Optional<User> userOpt = userServiceImpl.findByEmail(email);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "User not found"));
        }

        User user = userOpt.get();
        if (user.getPin() == null || !user.getPin().equals(pin)) {
            return ResponseEntity.status(401).body(Map.of("message", "Invalid PIN"));
        }

        String token = jwtService.generateToken(user);
        return ResponseEntity.ok(Map.of(
                "message", "Login successful",
                "token", token,
                "profile", user
        ));
    }


}
