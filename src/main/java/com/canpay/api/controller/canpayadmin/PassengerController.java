package com.canpay.api.controller.canpayadmin;

import com.canpay.api.dto.Dashboard.BankAccountDto;
import com.canpay.api.dto.Dashboard.Passenger.PassengerWalletDto;
import com.canpay.api.dto.Dashboard.Passenger.PassengerDto;
import com.canpay.api.entity.BankAccount;
import com.canpay.api.entity.PassengerWallet;
import com.canpay.api.entity.User;
import com.canpay.api.entity.User.UserRole;
import com.canpay.api.repository.dashboard.DBankAccountRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

import com.canpay.api.dto.Dashboard.Passenger.PassengerRegistrationRequestDto;
import com.canpay.api.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.ArrayList;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/canpay-admin")
public class PassengerController {

    private final UserRepository userRepository;
    private final DBankAccountRepository bankAccountRepository;
    @Autowired
    public PassengerController(UserRepository userRepository, DBankAccountRepository bankAccountRepository) {
        this.userRepository = userRepository;
        this.bankAccountRepository = bankAccountRepository;
    }

    // Create a new passenger     
    @PostMapping("/passengers")
    @Transactional
    public ResponseEntity<?> addPassenger(@RequestBody PassengerRegistrationRequestDto request) {
        System.out.println("Request received: " + request); // Debug log

        if (request.getName() == null || request.getName().isBlank() ||
            request.getNic() == null || request.getNic().isBlank() ||
            request.getEmail() == null || request.getEmail().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Name, NIC, and Email Address are required."
            ));
        }
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
    
        User passenger = new User();
        passenger.setName(request.getName());
        passenger.setNic(request.getNic());
        passenger.setRole(UserRole.PASSENGER);
        passenger.setEmail(request.getEmail());
        passenger.setPhotoUrl(request.getProfilePhotoUrl());
    
        PassengerWallet passengerWallet = new PassengerWallet(passenger);
        passenger.setPassengerWallet(passengerWallet);
    
        passenger = userRepository.save(passenger);
    
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
                bankAccountRepository.saveAll(bankAccounts);
            }
        }
    
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

        List<PassengerDto> passengerDtos = passengers.stream()
                .map(PassengerDto::new)
                .collect(Collectors.toList());

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
                            .map(acc -> new BankAccountDto(acc))
                            .collect(Collectors.toList());

                    PassengerWalletDto wallet = new PassengerWalletDto(user.getPassengerWallet());

                    return ResponseEntity.ok(
                            Map.of(
                                    "success", true,
                                    "message", "Passenger details",
                                    "data", Map.of(
                                            "user", new PassengerDto(user),
                                            "bankAccounts", bankAccounts,
                                            "wallet", wallet
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

    // Edit passenger information
    @PutMapping("/passengers/{id}")
    @Transactional
    public ResponseEntity<?> editPassenger(@PathVariable UUID id, @RequestBody PassengerRegistrationRequestDto request) {
        return userRepository.findById(id)
                .map(user -> {
                    if (request.getName() != null && !request.getName().isBlank()) {                            
                        user.setName(request.getName());
                    }
                    if (request.getNic() != null && !request.getNic().isBlank()) {
                        if (userRepository.findByNic(request.getNic()).isPresent() && !user.getNic().equals(request.getNic())) {
                            return ResponseEntity.badRequest().body(Map.of(
                                    "success", false,
                                    "message", "NIC already exists."
                            ));
                        }
                        user.setNic(request.getNic());
                    }
                    if (request.getEmail() != null && !request.getEmail().isBlank()) {
                        if (userRepository.findByEmail(request.getEmail()).isPresent() && !user.getEmail().equals(request.getEmail())) {
                            return ResponseEntity.badRequest().body(Map.of(
                                    "success", false,
                                    "message", "Email Address already exists."
                            ));
                        }
                        user.setEmail(request.getEmail());
                    }
                    if (request.getProfilePhotoUrl() != null && !request.getProfilePhotoUrl().isBlank()) {
                        user.setPhotoUrl(request.getProfilePhotoUrl());
                    }   
                    if (request.getBankAccounts() != null) {
                        // Remove all existing bank accounts for this user
                        List<BankAccount> existingAccounts = user.getBankAccounts();
                        if (existingAccounts != null && !existingAccounts.isEmpty()) {
                            bankAccountRepository.deleteAll(existingAccounts);
                            user.getBankAccounts().clear();
                        }
                        // Add new bank accounts
                        List<BankAccount> bankAccounts = request.getBankAccounts().stream()
                                .map(bankDto -> {
                                    BankAccount bankAccount = new BankAccount();
                                    bankAccount.setAccountName(bankDto.getAccountName());
                                    bankAccount.setAccountNumber(bankDto.getAccountNumber());
                                    bankAccount.setBankName(bankDto.getBankName());
                                    bankAccount.setUser(user);
                                    return bankAccount;
                                })
                                .collect(Collectors.toList());
                        bankAccountRepository.saveAll(bankAccounts);
                        user.getBankAccounts().addAll(bankAccounts);
                    }               

                    User updatedUser = userRepository.save(user);

                    List<BankAccountDto> bankAccounts = updatedUser.getBankAccounts().stream()
                            .map(BankAccountDto::new)
                            .collect(Collectors.toList());
                    PassengerWalletDto wallet = new PassengerWalletDto(updatedUser.getPassengerWallet());
                    return ResponseEntity.ok(
                            Map.of(
                                    "success", true,
                                    "message", "Passenger updated successfully.",
                                    "data", Map.of(
                                            "user", new PassengerDto(updatedUser),
                                            "bankAccounts", bankAccounts,
                                            "wallet", wallet
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

    // Delete a passenger
    @DeleteMapping("/passengers/{id}")
    @Transactional
    public ResponseEntity<?> deletePassenger(@PathVariable UUID id) {
        return userRepository.findById(id)
                .map(user -> {
                    userRepository.delete(user);
                    return ResponseEntity.ok(
                            Map.of(
                                    "success", true,    
                                    "message", "Passenger deleted successfully."
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
