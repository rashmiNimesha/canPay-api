package com.canpay.api.controller.canpayadmin;

import com.canpay.api.dto.BankAccountDto;
import com.canpay.api.entity.BankAccount;
import com.canpay.api.repository.BankAccountRepository;
import com.canpay.api.service.WalletService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

import com.canpay.api.dto.UserDto;
import com.canpay.api.dto.Dashboard.Passenger.PassengerRegistrationRequest;
import com.canpay.api.entity.User;
import com.canpay.api.repository.user.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;

@RestController
@RequestMapping("api/v1/canpay-admin")
public class PassengerController {

    private final UserRepository userRepository;
    private final BankAccountRepository bankAccountRepository;
    private final WalletService walletService;

    @Autowired
    public PassengerController(UserRepository userRepository, BankAccountRepository bankAccountRepository, WalletService walletService) {
        this.userRepository = userRepository;
        this.bankAccountRepository = bankAccountRepository;
        this.walletService = walletService;
    }

    @GetMapping("/passengers")
    public ResponseEntity<?> getAllPassengers() {
        List<User> passengers = userRepository.findByRole("PASSENGER");

        List<UserDto> passengerDtos = passengers.stream()
                .map(UserDto::new)
                .toList();

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "message", "List of all passengers",
                        "data", passengerDtos
                )
        );
    }

    @GetMapping("/passengers/count")
    public ResponseEntity<?> getPassengerCount() {
        long count = userRepository.countPassengers();

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "message", "Total number of passengers",
                        "data", Map.of("passengerCount", count)
                )
        );
    }

    // Create a new passenger     
    @PostMapping("/passengers/add")
    @Transactional
    public ResponseEntity<?> addPassenger(@RequestBody PassengerRegistrationRequest request) {
        if (request.getBankAccounts() != null) {
            System.out.println("Received bank accounts:");
            request.getBankAccounts().forEach(bankAccountDto -> 
            System.out.println(bankAccountDto.toString())
            );
        } else {
            System.out.println("No bank accounts provided.");
        }
        // Validate required fields
        if (request.getName() == null || request.getName().isBlank() ||
            request.getNic() == null || request.getNic().isBlank() ||
            request.getEmail() == null || request.getEmail().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Name, NIC, and Email Address are required."
            ));
        }
        // Check for duplicate NIC or Email
        if (userRepository.findByNic(request.getNic()).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "NIC already exists."
            ));
        }
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Email Address already exists."
            ));
        }
        // Create User entity
        User passenger = new User();
        passenger.setName(request.getName());
        passenger.setNic(request.getNic());
        passenger.setRole("PASSENGER");
        passenger.setEmail(request.getEmail());
        passenger.setProfilePhotoUrl(request.getProfilePhotoUrl()); // Optional
        passenger.setWalletBalance(0.0);
        passenger = userRepository.save(passenger);
        // Save bank accounts if provided
        if (request.getBankAccounts() != null && !request.getBankAccounts().isEmpty()) {
            List<BankAccount> bankAccounts = new ArrayList<>();
            for (BankAccountDto bankDto : request.getBankAccounts()) {
                if (bankDto.getBank() != null && !bankDto.getBank().isBlank() && bankDto.getAccountNumber() != 0 && bankDto.getAccountHolderName() != null && !bankDto.getAccountHolderName().isBlank()) {
                    BankAccount bankAccount = new BankAccount();
                    bankAccount.setAccountHolderName(bankDto.getAccountHolderName());
                    bankAccount.setAccountNumber(bankDto.getAccountNumber());
                    bankAccount.setBankName(bankDto.getBank());
                    bankAccount.setUser(passenger);
                    bankAccounts.add(bankAccount);
                }
            }
            if (!bankAccounts.isEmpty()) {
                bankAccountRepository.saveAll(bankAccounts);
                // Attach to user entity for consistency
                passenger.setBankAccounts(bankAccounts);
                userRepository.save(passenger);
            }
        }
        
        
        // Wallet is created by default with balance 0.0 (see User entity)
        // Optionally, you can call walletService.rechargeWallet if you want to initialize or trigger wallet logic
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Passenger created successfully.",
                "data", Map.of("passengerId", passenger.getId())
        ));
    }
}
