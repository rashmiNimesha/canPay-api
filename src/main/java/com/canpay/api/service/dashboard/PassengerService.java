package com.canpay.api.service.dashboard;

import com.canpay.api.entity.BankAccount;
import com.canpay.api.entity.PassengerWallet;
import com.canpay.api.entity.User;
import com.canpay.api.entity.User.UserRole;
import com.canpay.api.dto.Dashboard.BankAccountDto;
import com.canpay.api.dto.Dashboard.Passenger.PassengerDto;
import com.canpay.api.dto.Dashboard.Passenger.PassengerRegistrationRequestDto;
import com.canpay.api.dto.Dashboard.Passenger.PassengerWalletDto;
import com.canpay.api.repository.dashboard.DBankAccountRepository;
import com.canpay.api.repository.dashboard.DUserRepository;
import com.canpay.api.repository.dashboard.DPassengerWalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PassengerService {

    // Repository for User entities
    private final DUserRepository userRepository;
    // Repository for BankAccount entities
    private final DBankAccountRepository bankAccountRepository;
    // Repository for PassengerWallet entities
    private final DPassengerWalletRepository passengerWalletRepository;

    @Autowired
    public PassengerService(DUserRepository userRepository, DBankAccountRepository bankAccountRepository,
            DPassengerWalletRepository passengerWalletRepository) {
        this.userRepository = userRepository;
        this.bankAccountRepository = bankAccountRepository;
        this.passengerWalletRepository = passengerWalletRepository;
    }

    /**
     * Adds a new passenger to the system.
     * Validates required fields and uniqueness of NIC and Email.
     * Optionally adds bank accounts if provided.
     */
    @Transactional
    public Map<String, Object> addPassenger(PassengerRegistrationRequestDto request) {
        // Validate required fields
        if (request.getName() == null || request.getName().isBlank() ||
                request.getNic() == null || request.getNic().isBlank() ||
                request.getEmail() == null || request.getEmail().isBlank()) {
            throw new IllegalArgumentException("Name, NIC, and Email Address are required.");
        }

        // Check for NIC uniqueness within PASSENGER role
        if (userRepository.findByNicAndRole(request.getNic(), UserRole.PASSENGER).isPresent()) {
            throw new IllegalArgumentException("NIC already exists for PASSENGER role.");
        }

        // Updated to enforce email uniqueness within specific role
        if (userRepository.findByEmailAndRole(request.getEmail(), UserRole.PASSENGER).isPresent()) {
            throw new IllegalArgumentException("Email Address already exists for PASSENGER role.");
        }

        // Create new User entity for the passenger
        User passenger = new User();
        passenger.setName(request.getName());
        passenger.setNic(request.getNic());
        passenger.setRole(UserRole.PASSENGER);
        passenger.setEmail(request.getEmail());
        passenger.setPhotoUrl(request.getProfilePhotoUrl());

        // Create and associate a wallet for the passenger
        PassengerWallet passengerWallet = new PassengerWallet(passenger);
        passengerWallet.setWalletNumber(generateUniqueWalletNumber());
        passenger.setPassengerWallet(passengerWallet);

        // Save the passenger to the database
        passenger = userRepository.save(passenger);

        // Add bank accounts if provided
        if (request.getBankAccounts() != null && !request.getBankAccounts().isEmpty()) {
            List<BankAccount> bankAccounts = new ArrayList<>();
            for (BankAccountDto bankDto : request.getBankAccounts()) {
                // Validate required bank account fields
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

            // Save all valid bank accounts
            if (!bankAccounts.isEmpty()) {
                bankAccountRepository.saveAll(bankAccounts);
            }
        }

        // Return success response with passenger ID
        return Map.of(
                "success", true,
                "message", "Passenger created successfully.",
                "data", Map.of("passengerId", passenger.getId()));
    }

    /**
     * Generates a unique 16-digit wallet number.
     */
    private String generateUniqueWalletNumber() {
        String walletNumber;
        SecureRandom random = new SecureRandom();
        do {
            walletNumber = String.format("%016d", Math.abs(random.nextLong()) % 1_0000_0000_0000_0000L);
        } while (passengerWalletRepository.findByWalletNumber(walletNumber).isPresent());
        return walletNumber;
    }

    /**
     * Retrieves all passengers as a list of PassengerDto.
     */
    public List<PassengerDto> getAllPassengers() {
        List<User> passengers = userRepository.findByRole(UserRole.PASSENGER);
        return passengers.stream()
                .map(PassengerDto::new)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a passenger by ID, including their bank accounts and wallet.
     */
    public Map<String, Object> getPassengerById(UUID id) {
        return userRepository.findById(id)
                .map(user -> {
                    // Use DBankAccountRepository to fetch bank accounts
                    List<BankAccountDto> bankAccounts = bankAccountRepository.findByUserId(user.getId()).stream()
                            .map(BankAccountDto::new)
                            .collect(Collectors.toList());

                    // Map wallet to DTO
                    PassengerWalletDto wallet = passengerWalletRepository.findByPassenger_Id(user.getId())
                            .map(PassengerWalletDto::new)
                            .orElseThrow(() -> new NoSuchElementException("Wallet not found"));

                    // Return passenger details
                    return Map.of(
                            "success", true,
                            "message", "Passenger details",
                            "data", Map.of(
                                    "user", new PassengerDto(user),
                                    "bankAccounts", bankAccounts,
                                    "wallet", wallet));
                })
                .orElseThrow(() -> new NoSuchElementException("Passenger not found"));
    }

    /**
     * Edits an existing passenger's details.
     * Updates fields if provided and replaces bank accounts if new ones are given.
     */
    @Transactional
    public Map<String, Object> editPassenger(UUID id, PassengerRegistrationRequestDto request) {
        User user = userRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Passenger not found"));

        // Update fields if provided
        if (request.getName() != null && !request.getName().isBlank()) {
            user.setName(request.getName());
        }
        if (request.getNic() != null && !request.getNic().isBlank()) {
            if (userRepository.findByNicAndRole(request.getNic(), UserRole.PASSENGER).isPresent()
                    && !user.getNic().equals(request.getNic())) {
                throw new IllegalArgumentException("NIC already exists for PASSENGER role.");
            }
            user.setNic(request.getNic());
        }
        // Updated editPassenger method to enforce email uniqueness within specific role
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            if (userRepository.findByEmailAndRole(request.getEmail(), UserRole.PASSENGER).isPresent()
                    && !user.getEmail().equals(request.getEmail())) {
                throw new IllegalArgumentException("Email Address already exists for PASSENGER role.");
            }
            user.setEmail(request.getEmail());
        }
        if (request.getProfilePhotoUrl() != null && !request.getProfilePhotoUrl().isBlank()) {
            user.setPhotoUrl(request.getProfilePhotoUrl());
        }

        // Replace bank accounts if provided
        if (request.getBankAccounts() != null) {
            replaceBankAccounts(user, request.getBankAccounts());
        }

        // Save updated user
        User updatedUser = userRepository.save(user);

        // Prepare response DTOs
        List<BankAccountDto> bankAccounts = bankAccountRepository.findByUserId(updatedUser.getId()).stream()
                .map(BankAccountDto::new)
                .collect(Collectors.toList());
        PassengerWalletDto wallet = new PassengerWalletDto(updatedUser.getPassengerWallet());

        return Map.of(
                "success", true,
                "message", "Passenger updated successfully.",
                "data", Map.of(
                        "user", new PassengerDto(updatedUser),
                        "bankAccounts", bankAccounts,
                        "wallet", wallet));
    }

    /**
     * Deletes a passenger by ID.
     */
    @Transactional
    public Map<String, Object> deletePassenger(UUID id) {
        userRepository.deleteById(id);
        return Map.of(
                "success", true,
                "message", "Passenger deleted successfully.");
    }

    /**
     * Returns the total count of passengers.
     */
    public long getPassengerCount() {
        return userRepository.countByRole(UserRole.PASSENGER);
    }

    /**
     * Replaces all bank accounts for a user with new ones in a transactional
     * manner.
     * This ensures atomicity - either all operations succeed or all fail.
     * 
     */
    @Transactional
    private void replaceBankAccounts(User user, List<BankAccountDto> bankAccountDtos) {
        // Delete existing bank accounts
        bankAccountRepository.deleteByUserId(user.getId());

        // Create new bank accounts
        List<BankAccount> bankAccounts = bankAccountDtos.stream()
                .map(bankDto -> {
                    BankAccount bankAccount = new BankAccount();
                    bankAccount.setAccountName(bankDto.getAccountName());
                    bankAccount.setAccountNumber(bankDto.getAccountNumber());
                    bankAccount.setBankName(bankDto.getBankName());
                    bankAccount.setUser(user);
                    return bankAccount;
                })
                .collect(Collectors.toList());

        // Save all new bank accounts
        if (!bankAccounts.isEmpty()) {
            bankAccountRepository.saveAll(bankAccounts);
        }
    }
}
