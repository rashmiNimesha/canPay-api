package com.canpay.api.controller.account;

import com.canpay.api.dto.UserDto;
import com.canpay.api.entity.BankAccount;
import com.canpay.api.entity.ResponseEntityBuilder;
import com.canpay.api.entity.User;
import com.canpay.api.jwt.JwtUtil;
import com.canpay.api.service.implementation.BankAccountServiceImpl;
import com.canpay.api.service.implementation.JwtService;
import com.canpay.api.service.implementation.UserServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/user-service")
public class AccountController {

    public final UserServiceImpl userService;
    private final JwtUtil jwtUtil;
    private final JwtService jwtService;
    private final BankAccountServiceImpl bankAccountService;

    public AccountController(UserServiceImpl userService, JwtUtil jwtUtil, JwtService jwtService, BankAccountServiceImpl bankAccountService) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.jwtService = jwtService;
        this.bankAccountService = bankAccountService;
    }


    @PatchMapping("/passenger-account")
    @PreAuthorize("hasRole('PASSENGER')")
    public ResponseEntity<?> updatePassengerAccount(@RequestBody Map<String, String> request) {

        String email = request.get("email"); // current email from token
        User user = null;
        if (email == null || email.isEmpty()) {
            return new ResponseEntityBuilder.Builder<String>()
                    .resultMessage("Email is required")
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .buildWrapped();
        }
        Optional<User> userOpt = userService.findByEmail(email);
        if (userOpt.isEmpty()) {
            return new ResponseEntityBuilder.Builder<String>()
                    .resultMessage("User not found")
                    .httpStatus(HttpStatus.NOT_FOUND)
                    .buildWrapped();
        }

        user = userOpt.get();

        if (request.containsKey("name")) {
            user = userService.updateName(email, request.get("name"));
        }

        String newEmail = request.get("newemail");
        boolean emailChanged = false;
        if (newEmail != null && !newEmail.equalsIgnoreCase(email)) {
            user = userService.updateEmail(email, newEmail);
            email = newEmail;
            emailChanged = true;
        }

        if (request.containsKey("accName") &&
                request.containsKey("accNo") &&
                request.containsKey("bank")) {
            try {
                long accNo = Long.parseLong(request.get("accNo"));
                userService.addBankAccount(email, request.get("accName"), request.get("bank"), accNo);
            } catch (NumberFormatException e) {
                return new ResponseEntityBuilder.Builder<String>()
                        .resultMessage("Invalid account number format")
                        .httpStatus(HttpStatus.BAD_REQUEST)
                        .buildWrapped();
            }
        }

        String token = emailChanged ? jwtService.generateToken(user) : null;
        UserDto userDto = new UserDto(user.getName(), user.getEmail(), user.getNic());

        Map<String, Object> data = new HashMap<>();
        data.put("profile", userDto);
        if (token != null) {
            data.put("token", token);
        }

        return new ResponseEntityBuilder.Builder<Map<String, Object>>()
                .resultMessage("Passenger account updated")
                .httpStatus(HttpStatus.OK)
                .body(data)
                .buildWrapped();

    }

    @GetMapping("/passengers/bank-account")
    @PreAuthorize("hasRole('PASSENGER')")
    public ResponseEntity<?> getBankAccounts(@RequestParam("email") String email) {
        if (email == null || email.isEmpty()) {
            return new ResponseEntityBuilder.Builder<String>()
                    .resultMessage("Email is required")
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .buildWrapped();
        }

        List<BankAccount> accounts = bankAccountService.getAccountsByEmail(email);

        return new ResponseEntityBuilder.Builder<List<BankAccount>>()
                .resultMessage("Bank accounts retrieved successfully")
                .httpStatus(HttpStatus.OK)
                .body(accounts)
                .buildWrapped();
    }

}