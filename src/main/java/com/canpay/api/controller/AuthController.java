package com.canpay.api.controller;

import com.canpay.api.entity.User;
import com.canpay.api.repository.user.UserRepository;
import com.canpay.api.service.UserSevice;
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
@RequestMapping("/api/auth")
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
        String phone = request.get("phone");
        otpService.sendOtp(phone);
        return ResponseEntity.ok(Map.of("message", "OTP sent successfully"));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody Map<String, String> request) {
        String phone = request.get("phone");
        String otp = request.get("otp");

        boolean valid = otpService.verifyOtp(phone, otp);
        if (!valid) return ResponseEntity.status(401).body(Map.of("message", "Invalid OTP"));

        Optional<User> existingUser = userServiceImpl.findByPhone(phone);
        if (existingUser.isPresent()) {
            String token = jwtService.generateToken(existingUser.get());
            return ResponseEntity.ok(Map.of(
                    "newUser", false,
                    "token", token,
                    "profile", existingUser.get()
            ));
        }

        return ResponseEntity.ok(Map.of("newUser", true, "message", "OTP verified"));
    }

    @PostMapping("/create-profile")
    public ResponseEntity<?> createProfile(@RequestBody Map<String, String> request) {
        String phone = request.get("phone");
        String name = request.get("name");

        User newUser = userServiceImpl.createPassenger(phone, name);
        String token = jwtService.generateToken(newUser);

        return ResponseEntity.ok(Map.of(
                "message", "Profile created successfully",
                "token", token,
                "profile", newUser
        ));
    }

}
