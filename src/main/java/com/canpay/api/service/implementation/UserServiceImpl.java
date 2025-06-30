package com.canpay.api.service.implementation;

import com.canpay.api.entity.BankAccount;
import com.canpay.api.entity.User;
import com.canpay.api.entity.User.UserRole;
import com.canpay.api.repository.UserRepository;
import com.canpay.api.service.UserSevice;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UserServiceImpl implements UserSevice {

    private final UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Transactional
    @Override
    public User findUserByEmail(String email) {
        logger.debug("Finding user by email: {}", email);
        // Replace findByEmail with a check for role if needed
        // This method may need to be adjusted based on usage
        return userRepository.findByEmailAndRole(email, UserRole.PASSENGER) // Default to PASSENGER or adjust
                .orElseThrow(() -> {
                    logger.error("User not found for email: {}", email);
                    return new RuntimeException("User not found for email: " + email);
                });
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
    public User updatePassengerProfile(String email, String name, String nic, String accName, String bank, long accNo, UserRole role) {
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
            throw new RuntimeException("Failed to update profile: " + (e.getMessage() != null ? e.getMessage() : "Database error"));
        }
    }

    @Transactional
    @Override
    public User updateOperatorProfile(String email, String name, String nic, String profileImage, UserRole role) {
        logger.debug("Updating OPERATOR profile for email: {}, name: {}, nic: {}, profileImage: {}", email, name, nic, profileImage);

        User user = userRepository.findByEmailAndRole(email, role)
                .orElseThrow(() -> {
                    logger.error("User not found for email: {} and role: {}", email, role);
                    return new RuntimeException("User not found for email: " + email + " and role: " + role);
                });

        user.setName(name);
        user.setNic(nic);
        user.setPhotoUrl(profileImage);
        user.setStatus(User.UserStatus.ACTIVE);

        User updatedUser = userRepository.save(user);
        logger.info("OPERATOR profile updated successfully for email: {}", email);
        return updatedUser;
    }


    @Transactional
    @Override
    public User updateOwnerProfile(String email, String name, String nic, String profileImage, String accName, String bank, long accNo, UserRole role) {
        logger.debug("Updating OWNER profile for email: {}, name: {}, nic: {}, profileImage: {}, accName: {}, bank: {}, accNo: {}",
                email, name, nic, profileImage, accName, bank, accNo);

        User user = userRepository.findByEmailAndRole(email, role)
                .orElseThrow(() -> {
                    logger.error("User not found for email: {} and role: {}", email, role);
                    return new RuntimeException("User not found for email: " + email + " and role: " + role);
                });

        user.setName(name);
        user.setNic(nic);
        user.setPhotoUrl(profileImage);

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


    public User updateName(String email, String name) {
        User user = getUserOrThrow(email);
        user.setName(name);
        return userRepository.save(user);
    }

    public User updateEmail(String oldEmail, String newEmail) {
        Optional<User> userOpt = userRepository.findByEmail(oldEmail);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        if (userRepository.existsByEmail(newEmail)) {
            throw new RuntimeException("Email already in use");
        }
        User user = userOpt.get();
        user.setEmail(newEmail);
        return userRepository.save(user);
    }

    public User addBankAccount(String email, String accName, String bank, long accNo) {
        User user = getUserOrThrow(email);
        boolean exists = user.getBankAccounts().stream()
                .anyMatch(acc -> acc.getAccountNumber() == accNo);
        if (!exists) {
            BankAccount account = new BankAccount();
            account.setAccountName(accName);
            account.setBankName(bank);
            account.setAccountNumber(accNo);
            account.setUser(user);
            user.getBankAccounts().add(account);
            userRepository.save(user);
        }
        return userRepository.save(user);
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


}
