package com.canpay.api.controller;

import com.canpay.api.dto.UserDto;
import com.canpay.api.entity.ResponseEntityBuilder;
import com.canpay.api.entity.User;
import com.canpay.api.service.implementation.JwtService;
import com.canpay.api.service.implementation.OTPService;
import com.canpay.api.service.implementation.UserServiceImpl;
import org.springframework.http.HttpStatus;
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
        if (email == null || email.isBlank()) {
            return new ResponseEntityBuilder.Builder<Void>()
                    .resultMessage("Email is required")
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .buildWrapped();
        }

        otpService.sendOtp(email);
        return new ResponseEntityBuilder.Builder<Void>()
                .resultMessage("OTP sent successfully")
                .httpStatus(HttpStatus.OK)
                .buildWrapped();
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String otp = request.get("otp");
        String role = request.getOrDefault("role", "").toUpperCase();

        if (email == null || otp == null || role.isEmpty()) {
            return new ResponseEntityBuilder.Builder<Map<String, Object>>()
                    .resultMessage("Missing required fields")
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .buildWrapped();
        }

        boolean valid = otpService.verifyOtp(email, otp);
        System.out.println("OTP verified");

        if (!valid) {
            return new ResponseEntityBuilder.Builder<Map<String, Object>>()
                    .resultMessage("Invalid OTP")
                    .httpStatus(HttpStatus.UNAUTHORIZED)
                    .buildWrapped();
        }

        Optional<User> existingUser = userServiceImpl.findByEmail(email);

        if (existingUser.isPresent()) {
            String token = jwtService.generateToken(existingUser.get());

            Map<String, Object> responseData = Map.of(
                    "newUser", false,
                    "token", token,
                    "profile", new UserDto(existingUser.get()));


            return new ResponseEntityBuilder.Builder<Map<String, Object>>()
                    .resultMessage("OTP verified successfully")
                    .httpStatus(HttpStatus.OK)
                    .body(responseData)
                    .buildWrapped();
        }

        System.out.println("Going to create new account with role: " + role);
        userServiceImpl.registerWithEmail(email, role);
        System.out.println("New account created after OTP verification");

        Map<String, Object> responseData = Map.of(
                "newUser", true
        );

        return new ResponseEntityBuilder.Builder<Map<String, Object>>()
                .resultMessage("OTP verified and new user registered")
                .httpStatus(HttpStatus.OK)
                .body(responseData)
                .buildWrapped();
    }


    @PostMapping("/create-profile")
    public ResponseEntity<?> createProfile(@RequestBody Map<String, String> request) {
        System.out.println("came create acc");

        String email = request.get("email");
        String name = request.get("name");
        String nic = request.get("nic");
        String role = request.getOrDefault("role", "PASSENGER").toUpperCase();


        if (email == null || name == null || nic == null) {
            return new ResponseEntityBuilder.Builder<Map<String, Object>>()
                    .resultMessage("Missing name/email/nic")
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .buildWrapped();

        }

        try {
            User user;
            switch (role) {
                case "PASSENGER":
                    String accNoStrP = request.get("accNo");
                    String bankP = request.get("bank");
                    String accNameP = request.get("accName");

                    if (accNoStrP == null || bankP == null || accNameP == null)
                        return new ResponseEntityBuilder.Builder<Map<String, Object>>()
                                .resultMessage("Missing bank details")
                                .httpStatus(HttpStatus.BAD_REQUEST)
                                .buildWrapped();

                    long accNoP;
                    try {
                        accNoP = Long.parseLong(accNoStrP);
                    } catch (NumberFormatException e) {
                        return new ResponseEntityBuilder.Builder<Map<String, Object>>()
                                .resultMessage("Invalid account number")
                                .httpStatus(HttpStatus.BAD_REQUEST)
                                .buildWrapped();                    }

                    user = userServiceImpl.updateProfileWithBankAccount(email, name, nic, accNameP, bankP, accNoP);
                    break;

                case "OPERATOR":
                    String profileImageOp = request.get("profileImage");
                    if (profileImageOp == null)
                        return new ResponseEntityBuilder.Builder<Map<String, Object>>()
                                .resultMessage("Missing profile image")
                                .httpStatus(HttpStatus.BAD_REQUEST)
                                .buildWrapped();
                    user = userServiceImpl.updateOperatorProfile(email, name, nic, profileImageOp);
                    break;

                case "OWNER":
                    String profileImageOw = request.get("profileImage");
                    String accNoStrOw = request.get("accNo");
                    String bankOw = request.get("bank");
                    String accNameOw = request.get("accName");

                    if (profileImageOw == null || accNoStrOw == null || bankOw == null || accNameOw == null)
                        return new ResponseEntityBuilder.Builder<Map<String, Object>>()
                                .resultMessage("Missing owner data")
                                .httpStatus(HttpStatus.BAD_REQUEST)
                                .buildWrapped();
                    long accNoOw;
                    try {
                        accNoOw = Long.parseLong(accNoStrOw);
                    } catch (NumberFormatException e) {
                        return new ResponseEntityBuilder.Builder<Map<String, Object>>()
                                .resultMessage("Invalid account number")
                                .httpStatus(HttpStatus.BAD_REQUEST)
                                .buildWrapped();                    }

                    user = userServiceImpl.updateOwnerProfile(email, name, nic, profileImageOw, accNameOw, bankOw, accNoOw);
                    break;

                default:
                    return new ResponseEntityBuilder.Builder<Map<String, Object>>()
                            .resultMessage("Invalid role")
                            .httpStatus(HttpStatus.BAD_REQUEST)
                            .buildWrapped();            }

            String token = jwtService.generateToken(user);
            UserDto userDto = new UserDto(user.getRole(), user.getName(), user.getEmail(), user.getNic());

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
            return new ResponseEntityBuilder.Builder<Map<String, Object>>()
                    .resultMessage(e.getMessage())
                    .httpStatus(HttpStatus.NOT_FOUND)
                    .buildWrapped();        }
    }
    

}
