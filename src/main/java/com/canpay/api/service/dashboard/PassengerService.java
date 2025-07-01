package com.canpay.api.service.dashboard;

import com.canpay.api.entity.BankAccount;
import com.canpay.api.entity.PassengerWallet;
import com.canpay.api.entity.User;
import com.canpay.api.entity.User.UserRole;
import com.canpay.api.entity.User.UserStatus;
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

import java.io.IOException;
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
            throw new IllegalArgumentException("NIC already exists for another passenger.");
        }

        // Enforce email uniqueness within specific role
        if (userRepository.findByEmailAndRole(request.getEmail(), UserRole.PASSENGER).isPresent()) {
            throw new IllegalArgumentException("Email Address already exists for another passenger.");
        }

        // Create new User entity from DTO manually
        User passenger = new User();
        passenger.setName(request.getName());
        passenger.setNic(request.getNic());
        passenger.setEmail(request.getEmail());
        passenger.setPhotoUrl(request.getPhoto());
        passenger.setRole(UserRole.PASSENGER);
        passenger.setStatus(UserStatus.ACTIVE);

        // Save the photo to system storage and set the photo URL
        if (request.getPhoto() != null && !request.getPhoto().isBlank()) {
            try {
                String photoPath = Utils.saveImage(request.getPhoto(), UUID.randomUUID().toString() + ".png");
                passenger.setPhotoUrl(photoPath);
            } catch (IOException e) {
                throw new RuntimeException("Failed to save passenger photo", e);
            }
        }

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
                        bankDto.getAccountNumber() != null && !bankDto.getAccountNumber().isBlank() &&
                        bankDto.getAccountName() != null && !bankDto.getAccountName().isBlank()) {
                    BankAccount bankAccount = new BankAccount();
                    bankAccount.setAccountName(bankDto.getAccountName());
                    bankAccount.setAccountNumber(Long.parseLong(bankDto.getAccountNumber()));
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

        // Return PassengerDto instead of UUID
        return passenger.getId();
    }

    /**
     * Retrieves all passengers as a list of PassengerDto.
     */
    public List<PassengerDto> getAllPassengers() {
        List<User> passengers = userRepository.findByRole(UserRole.PASSENGER);
        return passengers.stream()
                .map(user -> {
                    PassengerDto dto = new PassengerDto(user);
                    // Convert photo file path to data URL
                    if (user.getPhotoUrl() != null && !user.getPhotoUrl().isBlank()) {
                        dto.setPhoto(Utils.convertImageToDataUrl(user.getPhotoUrl()));
                    }
                    // Get wallet information
                    PassengerWalletDto walletDto = passengerWalletRepository.findByPassenger_Id(user.getId())
                            .map(PassengerWalletDto::new)
                            .orElseThrow(() -> new NoSuchElementException("Wallet not found"));
                    dto.setWallet(walletDto);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a passenger by ID, including their bank accounts and wallet.
     */
    public PassengerDto getPassengerById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Passenger not found"));

        // Create passenger DTO from entity
        PassengerDto passengerDto = new PassengerDto(user);

        // Convert photo file path to data URL
        if (user.getPhotoUrl() != null && !user.getPhotoUrl().isBlank()) {
            passengerDto.setPhoto(Utils.convertImageToDataUrl(user.getPhotoUrl()));
        }

        // Fetch and set bank accounts
        List<DBankAccountDto> bankAccounts = bankAccountRepository.findByUserId(user.getId()).stream()
                .map(DBankAccountDto::new)
                .collect(Collectors.toList());
        passengerDto.setBankAccounts(bankAccounts);

        // Fetch and set wallet
        PassengerWalletDto wallet = passengerWalletRepository.findByPassenger_Id(user.getId())
                .map(PassengerWalletDto::new)
                .orElseThrow(() -> new NoSuchElementException("Wallet not found"));
        passengerDto.setWallet(wallet);

        return passengerDto;
    }

    /**
     * Edits an existing passenger's details.
     * Updates fields if provided and replaces bank accounts if new ones are given.
     */
    @Transactional
    public PassengerDto editPassenger(UUID id, PassengerRegistrationRequestDto request) {
        User user = userRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Passenger not found"));

        // Update fields if provided
        if (request.getName() != null && !request.getName().isBlank()) {
            user.setName(request.getName());
        }
        if (request.getNic() != null && !request.getNic().isBlank()) {
            if (userRepository.findByNicAndRole(request.getNic(), UserRole.PASSENGER).isPresent()
                    && !user.getNic().equals(request.getNic())) {
                throw new IllegalArgumentException("NIC already exists for another passenger.");
            }
            user.setNic(request.getNic());
        }
        // Updated editPassenger method to enforce email uniqueness within specific role
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            if (userRepository.findByEmailAndRole(request.getEmail(), UserRole.PASSENGER).isPresent()
                    && !user.getEmail().equals(request.getEmail())) {
                throw new IllegalArgumentException("Email Address already exists for another passenger.");
            }
            user.setEmail(request.getEmail());
        }
        if (request.getPhoto() != null && !request.getPhoto().isBlank()) {
            try {
                // Delete the old photo
                Utils.deleteImage(user.getPhotoUrl());

                // Save the new photo
                String photoPath = Utils.saveImage(request.getPhoto(), UUID.randomUUID().toString() + ".png");
                user.setPhotoUrl(photoPath);
            } catch (IOException e) {
                throw new RuntimeException("Failed to save passenger photo", e);
            }
        }

        // Replace bank accounts if provided
        if (request.getBankAccounts() != null) {
            replaceBankAccounts(user, request.getBankAccounts());
        }

        // Save updated user
        User updatedUser = userRepository.save(user);

        // Create response DTO with related data
        PassengerDto passengerDto = new PassengerDto(updatedUser);

        // Fetch and set bank accounts
        List<DBankAccountDto> bankAccounts = bankAccountRepository.findByUserId(updatedUser.getId()).stream()
                .map(DBankAccountDto::new)
                .collect(Collectors.toList());
        passengerDto.setBankAccounts(bankAccounts);

        // Set wallet
        PassengerWalletDto wallet = new PassengerWalletDto(updatedUser.getPassengerWallet());
        passengerDto.setWallet(wallet);

        return passengerDto;
    }

    /**
     * Changes the status of a passenger by ID.
     */
    @Transactional
    public void changePassengerStatus(UUID id, String newStatus) {
        User user = userRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Passenger not found"));
        String cleanStatus = newStatus.replace("\"", "").trim();

        // Validate newStatus against UserStatus enum
        try {
            UserStatus status = Arrays.stream(UserStatus.values())
                    .filter(enumValue -> enumValue.name().equalsIgnoreCase(
                            cleanStatus))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Invalid user status provided."));
            user.setStatus(status);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid user status provided.");
        }

        // Save the updated user status
        userRepository.save(user);
    }

    /**
     * Deletes a passenger by ID.
     */
    @Transactional
    public void deletePassenger(UUID id) {
        User user = userRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Passenger not found"));

        // Delete the associated photo
        Utils.deleteImage(user.getPhotoUrl());

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
                    bankAccount.setAccountNumber(Long.parseLong(bankDto.getAccountNumber()));
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
