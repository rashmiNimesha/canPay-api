package com.canpay.api.controller.canpayadmin;

import com.canpay.api.dto.BankAccountDto;
import com.canpay.api.dto.UserDto;
import com.canpay.api.entity.BankAccount;
import com.canpay.api.entity.PassengerWallet;
import com.canpay.api.entity.User;
import com.canpay.api.entity.User.UserRole;
import com.canpay.api.repository.BankAccountRepository;
import com.canpay.api.service.WalletService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

import com.canpay.api.dto.Dashboard.Passenger.PassengerRegistrationRequest;
import com.canpay.api.repository.user.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.ArrayList;

@RestController
@RequestMapping("api/v1/canpay-admin")
public class PassengerController {

    private final UserRepository userRepository;
    private final BankAccountRepository bankAccountRepository;
    @Autowired
    public PassengerController(UserRepository userRepository, BankAccountRepository bankAccountRepository, WalletService walletService) {
        this.userRepository = userRepository;
        this.bankAccountRepository = bankAccountRepository;
    }

    // Create a new passenger     
    @PostMapping("/passengers/add")
    @Transactional
    public ResponseEntity<?> addPassenger(@RequestBody PassengerRegistrationRequest request) {
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
        passenger.setRole(UserRole.PASSENGER);
        passenger.setEmail(request.getEmail());
        passenger.setPhotoUrl(request.getProfilePhotoUrl()); // Optional
        passenger = userRepository.save(passenger);
        
        // Create passenger wallet
        PassengerWallet passengerWallet = new PassengerWallet(passenger);
        passenger.setPassengerWallet(passengerWallet);
        passenger = userRepository.save(passenger);
        // Save bank accounts if provided
        if (request.getBankAccounts() != null && !request.getBankAccounts().isEmpty()) {
            List<BankAccount> bankAccounts = new ArrayList<>();
            for (BankAccountDto bankDto : request.getBankAccounts()) {
                if (bankDto.getBankName() != null && !bankDto.getBankName().isBlank() && 
                    bankDto.getAccountNumber() != null && 
                    bankDto.getAccountName() != null && !bankDto.getAccountName().isBlank()) {
                    BankAccount bankAccount = new BankAccount();
                    bankAccount.setAccountName(bankDto.getAccountName());
                    bankAccount.setAccountNumber(bankDto.getAccountNumber()); 
                    bankAccount.setBankName(bankDto.getBankName());
                    bankAccount.setUser(passenger);
                    bankAccounts.add(bankAccount);
                }
            }
            if (!bankAccounts.isEmpty()) {
                System.out.println("Saving " + bankAccounts.size() + " bank accounts to database");
                List<BankAccount> savedAccounts = bankAccountRepository.saveAll(bankAccounts);
                System.out.println("Saved " + savedAccounts.size() + " bank accounts successfully");
            } else {
                System.out.println("No valid bank accounts to save");
            }
        } else {
            System.out.println("No bank accounts provided in request");
        }
        
        // Passenger wallet is created with default balance 0.0
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Passenger created successfully.",
                "data", Map.of("passengerId", passenger.getId())
        ));
    }

    // Get all passengers
    @GetMapping("/passengers")
    public ResponseEntity<?> getAllPassengers() {
        List<User> passengers = userRepository.findByRole(UserRole.PASSENGER);

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

    // Get passenger information by ID
    @GetMapping("/passengers/{id}")
    public ResponseEntity<?> getPassengerById(@PathVariable UUID id) {
        return userRepository.findById(id)
                .map(user -> {
                    List<BankAccountDto> bankAccounts = user.getBankAccounts().stream()
                            .map(acc -> new BankAccountDto(acc.getBankName(), acc.getAccountNumber(), acc.getAccountName()))
                            .toList();

                    return ResponseEntity.ok(
                            Map.of(
                                    "success", true,
                                    "message", "Passenger details",
                                    "data", Map.of(
                                            "user", new UserDto(user),
                                            "bankAccounts", bankAccounts
                                    )
                            )
                    );
                })
                .orElse(ResponseEntity.status(404).body(
                        Map.of(
                                "success", false,
                                "message", "Passenger not found"
                        )
                ));
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
}
