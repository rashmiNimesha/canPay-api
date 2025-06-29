package com.canpay.api.service.dashboard;

import com.canpay.api.entity.BankAccount;
import com.canpay.api.entity.PassengerWallet;
import com.canpay.api.entity.User;
import com.canpay.api.entity.User.UserRole;
import com.canpay.api.lib.Utils;
import com.canpay.api.dto.Dashboard.DBankAccountDto;
import com.canpay.api.dto.Dashboard.Passenger.PassengerDto;
import com.canpay.api.dto.Dashboard.Passenger.PassengerRegistrationRequestDto;
import com.canpay.api.dto.Dashboard.Passenger.PassengerWalletDto;
import com.canpay.api.repository.dashboard.DBankAccountRepository;
import com.canpay.api.repository.dashboard.DUserRepository;
import com.canpay.api.repository.dashboard.DPassengerWalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public UUID addPassenger(PassengerRegistrationRequestDto request) {
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

        // Enforce email uniqueness within specific role
        if (userRepository.findByEmailAndRole(request.getEmail(), UserRole.PASSENGER).isPresent()) {
            throw new IllegalArgumentException("Email Address already exists for PASSENGER role.");
        }

        // Create new User entity for the passenger (not saved yet)
        User passenger = new User();
        passenger.setName(request.getName());
        passenger.setNic(request.getNic());
        passenger.setRole(UserRole.PASSENGER);
        passenger.setEmail(request.getEmail());
        passenger.setPhotoUrl(request.getProfilePhotoUrl());

        // Create and associate a wallet for the passenger
        PassengerWallet passengerWallet = new PassengerWallet(passenger);
        passengerWallet.setWalletNumber(Utils.generateUniqueWalletNumber(passengerWalletRepository));
        passenger.setPassengerWallet(passengerWallet);

        // Add bank accounts if provided (create and set to user)
        List<BankAccount> bankAccounts = new ArrayList<>();
        if (request.getBankAccounts() != null && !request.getBankAccounts().isEmpty()) {
            for (DBankAccountDto bankDto : request.getBankAccounts()) {
                // Validate required bank account fields
                if (bankDto.getBankName() != null && !bankDto.getBankName().isBlank() &&
                        bankDto.getAccountNumber() != null &&
                        bankDto.getAccountName() != null && !bankDto.getAccountName().isBlank()) {
                    BankAccount bankAccount = new BankAccount();
                    bankAccount.setAccountName(bankDto.getAccountName());
                    bankAccount.setAccountNumber(bankDto.getAccountNumber());
                    bankAccount.setBankName(bankDto.getBankName());
                    bankAccount.setDefault(bankDto.isDefault());
                    bankAccount.setUser(passenger);
                    bankAccounts.add(bankAccount);
                }
            }
        }

        // If User entity has a collection for bank accounts, set it here
        passenger.setBankAccounts(bankAccounts);

        // Save the passenger (with wallet and bank accounts set)
        passenger = userRepository.save(passenger);

        // Return passenger ID
        return passenger.getId();
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
                    List<DBankAccountDto> bankAccounts = bankAccountRepository.findByUserId(user.getId()).stream()
                            .map(DBankAccountDto::new)
                            .collect(Collectors.toList());

                    // Map wallet to DTO
                    PassengerWalletDto wallet = passengerWalletRepository.findByPassenger_Id(user.getId())
                            .map(PassengerWalletDto::new)
                            .orElseThrow(() -> new NoSuchElementException("Wallet not found"));

                    // Return passenger details
                    return Map.of(
                            "user", new PassengerDto(user),
                            "bankAccounts", bankAccounts,
                            "wallet", wallet);
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
        List<DBankAccountDto> bankAccounts = bankAccountRepository.findByUserId(updatedUser.getId()).stream()
                .map(DBankAccountDto::new)
                .collect(Collectors.toList());
        PassengerWalletDto wallet = new PassengerWalletDto(updatedUser.getPassengerWallet());

        return Map.of(
                "user", new PassengerDto(updatedUser),
                "bankAccounts", bankAccounts,
                "wallet", wallet);
    }

    /**
     * Deletes a passenger by ID.
     */
    @Transactional
    public void deletePassenger(UUID id) {
        userRepository.deleteById(id);
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
    private void replaceBankAccounts(User user, List<DBankAccountDto> bankAccountDtos) {
        // Delete existing bank accounts
        bankAccountRepository.deleteByUserId(user.getId());

        // Create new bank accounts
        List<BankAccount> bankAccounts = bankAccountDtos.stream()
                .map(bankDto -> {
                    BankAccount bankAccount = new BankAccount();
                    bankAccount.setAccountName(bankDto.getAccountName());
                    bankAccount.setAccountNumber(bankDto.getAccountNumber());
                    bankAccount.setBankName(bankDto.getBankName());
                    bankAccount.setDefault(bankDto.isDefault());
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
