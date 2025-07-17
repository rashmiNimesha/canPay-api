package com.canpay.api.service.implementation;

import com.canpay.api.entity.BankAccount;
import com.canpay.api.entity.User;
import com.canpay.api.entity.User.UserRole;
import com.canpay.api.entity.Wallet;
import com.canpay.api.lib.Utils;
import com.canpay.api.repository.UserRepository;
import com.canpay.api.repository.bankaccount.BankAccountRepository;
import com.canpay.api.repository.dashboard.DWalletRepository;
import com.canpay.api.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    private final BankAccountRepository bankAccountRepository;
    private final DWalletRepository walletRepository;
    @Value("${app.base-url}")
    private String baseUrl;

    public UserServiceImpl(UserRepository userRepository, BankAccountRepository bankAccountRepository,
            DWalletRepository walletRepository) {
        this.userRepository = userRepository;
        this.bankAccountRepository = bankAccountRepository;
        this.walletRepository = walletRepository;
    }

    @Transactional
    @Override
    public Optional<User> findUserByEmail(String email) {
        logger.debug("Finding user by email: {}", email);
        return userRepository.findByEmailAndRole(email, UserRole.PASSENGER);
    }

    public Optional<User> findByEmailAndRole(String email, UserRole role) {
        return userRepository.findByEmailAndRole(email, role);
    }

    public User registerWithEmail(String email, String roleStr) {
        UserRole role = UserRole.valueOf(roleStr.toUpperCase());

        // Check if email-role combination already exists
        if (userRepository.findByEmailAndRole(email, role).isPresent()) {
            throw new IllegalArgumentException("User already exists with this email and role.");
        }

        // Check if user has less than 3 roles
        long roleCount = countRolesByEmail(email);
        if (roleCount >= 3) {
            throw new IllegalArgumentException("User already has maximum of 3 roles.");
        }

        User user = new User();
        user.setEmail(email);
        user.setRole(role);
        user.setStatus(User.UserStatus.PENDING);
        return userRepository.save(user);
    }

    @Transactional
    @Override
    public User updatePassengerProfile(String email, String name, String nic, String accName, String bank, long accNo,
            UserRole role) {
        logger.debug("Updating PASSENGER profile for email: {}, name: {}, nic: {}, accName: {}, bank: {}, accNo: {}",
                email, name, nic, accName, bank, accNo);

        Optional<User> optionalUser = userRepository.findByEmailAndRole(email, role);
        if (!optionalUser.isPresent()) {
            logger.error("User not found for email: {} and role: {}", email, role);
            throw new RuntimeException("User not found");
        }

        User user = optionalUser.get();
        user.setName(name);
        user.setNic(nic);
        user.setStatus(User.UserStatus.ACTIVE);

        // Initialize bankAccounts list if null
        List<BankAccount> bankAccounts = user.getBankAccounts();
        if (bankAccounts == null) {
            bankAccounts = new ArrayList<>();
            user.setBankAccounts(bankAccounts);
        }

        // Create new BankAccount
        BankAccount bankAccount = new BankAccount();
        bankAccount.setBankName(bank);
        bankAccount.setAccountName(accName);
        bankAccount.setAccountNumber(accNo);
        bankAccount.setUser(user);
        // Set isDefault to true if this is the first account, false otherwise
        bankAccount.setDefault(bankAccounts.isEmpty());

        // If adding a new default account, unset default on existing accounts
        if (bankAccount.isDefault()) {
            for (BankAccount existingAccount : bankAccounts) {
                existingAccount.setDefault(false);
            }
        }

        bankAccounts.add(bankAccount);

        try {
            User updatedUser = userRepository.save(user);
            logger.info("PASSENGER profile updated successfully for email: {}", email);
            logger.debug("Updated user with bank account: {}", updatedUser);
            return updatedUser;
        } catch (Exception e) {
            logger.error("Failed to update PASSENGER profile for email: {}. Reason: {}", email, e.getMessage(), e);
            throw new RuntimeException(
                    "Failed to update profile: " + (e.getMessage() != null ? e.getMessage() : "Database error"));
        }
    }

    @Transactional
    @Override
    public User updateOperatorProfile(String email, String name, String nic, String profileImage, UserRole role) {
        logger.debug("Updating OPERATOR profile for email: {}, name: {}, nic: {}, profileImage: {}", email, name, nic,
                profileImage);

        User user = userRepository.findByEmailAndRole(email, role)
                .orElseThrow(() -> {
                    logger.error("User not found for email: {} and role: {}", email, role);
                    return new RuntimeException("User not found for email: " + email + " and role: " + role);
                });

        user.setName(name);
        user.setNic(nic);
        user.setStatus(User.UserStatus.ACTIVE);
        user.setPhotoUrl(handlePhotoUpload(profileImage, null));

        User updatedUser = userRepository.save(user);
        logger.info("OPERATOR profile updated successfully for email: {}", email);
        return updatedUser;
    }

    @Transactional
    @Override
    public User updateOwnerProfile(String email, String name, String nic, String profileImage, String accName,
            String bank, long accNo, UserRole role) {
        logger.debug(
                "Updating OWNER profile for email: {}, name: {}, nic: {}, profileImage: {}, accName: {}, bank: {}, accNo: {}",
                email, name, nic, profileImage, accName, bank, accNo);

        User user = userRepository.findByEmailAndRole(email, role)
                .orElseThrow(() -> {
                    logger.error("User not found for email: {} and role: {}", email, role);
                    return new RuntimeException("User not found for email: " + email + " and role: " + role);
                });

        user.setName(name);
        user.setNic(nic);
        user.setPhotoUrl(handlePhotoUpload(profileImage, null));

        List<BankAccount> bankAccounts = user.getBankAccounts();
        if (bankAccounts == null) {
            bankAccounts = new ArrayList<>();
            user.setBankAccounts(bankAccounts);
        }

        BankAccount bankAccount = new BankAccount();
        bankAccount.setAccountName(accName);
        bankAccount.setBankName(bank);
        bankAccount.setAccountNumber(accNo);
        bankAccount.setUser(user);
        bankAccount.setDefault(bankAccounts.isEmpty());

        if (bankAccount.isDefault()) {
            for (BankAccount existingAccount : bankAccounts) {
                existingAccount.setDefault(false);
            }
        }

        bankAccounts.add(bankAccount);
        user.setStatus(User.UserStatus.ACTIVE);

        User updatedUser = userRepository.save(user);
        logger.info("OWNER profile updated successfully for email: {}", email);
        return updatedUser;
    }

    @Transactional
    @Override
    public BankAccount addBankAccount(String email, String accountName, String bankName, long accountNumber,
            boolean isDefault) {
        logger.debug("Adding bank account for email: {}, accountNumber: {}", email, accountNumber);
        if (accountName == null || accountName.trim().isEmpty()) {
            logger.warn("Invalid account name for email: {}", email);
            throw new IllegalArgumentException("Account name cannot be empty");
        }
        if (bankName == null || bankName.trim().isEmpty()) {
            logger.warn("Invalid bank name for email: {}", email);
            throw new IllegalArgumentException("Bank name cannot be empty");
        }
        if (accountNumber <= 0) {
            logger.warn("Invalid account number: {} for email: {}", accountNumber, email);
            throw new IllegalArgumentException("Invalid account number");
        }
        User user = userRepository.findByEmailAndRole(email, UserRole.PASSENGER)
                .orElseThrow(() -> {
                    logger.error("User not found for email: {}", email);
                    return new RuntimeException("User not found for email: " + email);
                });
        BankAccount bankAccount = new BankAccount();
        bankAccount.setBankName(bankName.trim());
        bankAccount.setAccountNumber(accountNumber);
        bankAccount.setAccountName(accountName.trim());
        bankAccount.setDefault(isDefault);
        bankAccount.setCreatedAt(LocalDateTime.now());
        bankAccount.setUpdatedAt(LocalDateTime.now());
        bankAccount.setUser(user); // Set the User reference
        user.getBankAccounts().add(bankAccount);
        if (isDefault) {
            user.getBankAccounts().forEach(acc -> acc.setDefault(acc == bankAccount));
        }
        userRepository.save(user);
        logger.info("Added bank account for email: {}, accountNumber: {}", email, accountNumber);
        return bankAccount;
    }

    private User getUserOrThrow(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }

    public long countRolesByEmail(String email) {
        if (email == null || email.isBlank()) {
            logger.error("Invalid email provided for role count: {}", email);
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        long count = userRepository.countDistinctRolesByEmail(email);
        logger.debug("Role count for email {}: {}", email, count);
        return count;
    }

    @Override
    public User updateName(String email, String name) {
        logger.debug("Updating name for email: {}", email);
        if (name == null || name.trim().isEmpty()) {
            logger.warn("Invalid name provided for email: {}", email);
            throw new IllegalArgumentException("Name cannot be empty");
        }
        User user = userRepository.findByEmailAndRole(email, User.UserRole.PASSENGER)
                .orElseThrow(() -> {
                    logger.error("User not found for email: {}", email);
                    return new RuntimeException("User not found");
                });
        user.setName(name.trim());
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    @Override
    public User updateEmail(String email, String newEmail) {
        logger.debug("Updating email from {} to {}", email, newEmail);
        if (newEmail == null || newEmail.trim().isEmpty()
                || !newEmail.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            logger.warn("Invalid new email provided: {}", newEmail);
            throw new IllegalArgumentException("Invalid email format");
        }
        if (userRepository.findByEmailAndRole(newEmail, User.UserRole.PASSENGER).isPresent()) {
            logger.warn("New email already in use: {}", newEmail);
            throw new IllegalArgumentException("Email already in use");
        }
        User user = userRepository.findByEmailAndRole(email, User.UserRole.PASSENGER)
                .orElseThrow(() -> {
                    logger.error("User not found for email: {}", email);
                    return new RuntimeException("User not found");
                });
        user.setEmail(newEmail.trim());
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    public Optional<User> findUserById(UUID operatorId) {
        Optional<User> userOpt = userRepository.findById(operatorId);
        if (userOpt.isPresent()) {
            logger.debug("User found: {}", userOpt.get());
        } else {
            logger.warn("User not found with ID: {}", operatorId);
        }
        return userOpt;
    }

    public Map<String, Object> getUserFinancialDetails(String email, UserRole role) {
        logger.debug("Fetching financial details for user: {}", email);

        // Find user by email
        User user = userRepository.findByEmailAndRole(email, role)
                .orElseThrow(() -> {
                    logger.warn("User not found: {}", email);
                    return new RuntimeException("User not found");
                });

        // Find default bank account
        Optional<BankAccount> bankAccountOpt = bankAccountRepository.findByUserAndIsDefaultTrue(user);
        Map<String, Object> bankDetails = new HashMap<>();
        if (bankAccountOpt.isPresent()) {
            BankAccount bankAccount = bankAccountOpt.get();
            bankDetails.put("accountNumber", bankAccount.getAccountNumber());
            bankDetails.put("accountName", bankAccount.getAccountName());
        } else {
            logger.info("No default bank account found for user: {}", email);
            bankDetails.put("accountNumber", null);
            bankDetails.put("accountName", null);
        }

        // Find wallet balance
        Optional<Wallet> walletOpt = walletRepository.findByUser(user);
        Map<String, Object> walletDetails = new HashMap<>();
        if (walletOpt.isPresent()) {
            walletDetails.put("balance", walletOpt.get().getBalance());
        } else {
            logger.info("No wallet found for user: {}", email);
            walletDetails.put("balance", null);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("email", email);
        result.put("bankAccount", bankDetails);
        result.put("wallet", walletDetails);

        logger.info("Successfully fetched financial details for user: {}", email);
        return result;
    }

    private String handlePhotoUpload(String photo, String oldPhotoUrl) {
        if (photo == null || photo.isBlank())
            return oldPhotoUrl;
        if (photo.startsWith("http") || (baseUrl != null && photo.startsWith(baseUrl))) {
            return oldPhotoUrl; // Already a URL, don't update
        }
        try {
            if (oldPhotoUrl != null) {
                Utils.deleteFile(oldPhotoUrl);
            }
            return Utils.saveImage(photo, UUID.randomUUID().toString() + ".jpg");
        } catch (IOException e) {
            throw new RuntimeException("Failed to save user photo", e);
        }
    }
}
