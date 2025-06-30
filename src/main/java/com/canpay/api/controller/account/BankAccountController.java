package com.canpay.api.controller.account;

import com.canpay.api.dto.BankAccountDto;
import com.canpay.api.entity.User;
import com.canpay.api.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
}
