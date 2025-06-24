package com.canpay.api.controller;

import com.canpay.api.dto.BankAccountDto;
import com.canpay.api.entity.User;
import com.canpay.api.repository.user.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/bank-account")
public class BankAccountController {

    private final UserRepository userRepository;

    public BankAccountController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/by-email")
    public ResponseEntity<?> getBankAccountsByEmail(@RequestParam String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(404).body("User not found");
        }
        User user = userOpt.get();
        List<BankAccountDto> result = user.getBankAccounts().stream()
                .map(acc -> new BankAccountDto(acc.getBankName(), acc.getAccountNumber()))
                .toList();

        System.out.println("loaded bank accounts");
        return ResponseEntity.ok(result);

    }
}
