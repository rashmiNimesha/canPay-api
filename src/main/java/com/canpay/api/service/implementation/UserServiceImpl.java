package com.canpay.api.service.implementation;

import com.canpay.api.entity.BankAccount;
import com.canpay.api.entity.User;
import com.canpay.api.entity.User.UserRole;
import com.canpay.api.repository.user.UserRepository;
import com.canpay.api.service.UserSevice;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class UserServiceImpl implements UserSevice {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User registerWithEmail(String email, String roleString) {
        User user = new User();
        user.setEmail(email);
        
        // Convert string to enum
        UserRole role;
        try {
            role = UserRole.valueOf(roleString.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid user role: " + roleString);
        }
        
        user.setRole(role);
        return userRepository.save(user);
    }

    public User updateProfileWithBankAccount(String email, String name, String nic, String accName, String bank, long accNo) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        User user = userOpt.get();
        user.setName(name);
        user.setNic(nic);

        boolean alreadyExists = user.getBankAccounts().stream()
                .anyMatch(acc -> acc.getAccountNumber() == accNo);

        if (!alreadyExists) {
            BankAccount bankAccount = new BankAccount();
            bankAccount.setAccountName(name);
            bankAccount.setAccountNumber(accNo);
            bankAccount.setBankName(bank);
            bankAccount.setUser(user);
            user.getBankAccounts().add(bankAccount);
        }

        return userRepository.save(user);
    }

    @Override
    public User updateOperatorProfile(String email, String name, String nic, String profileImageBase64) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found with email: " + email);
        }
        User user = userOpt.get();

        if (user.getRole() != UserRole.OPERATOR) {
            throw new RuntimeException("User is not an OPERATOR");
        }

        user.setName(name);
        user.setNic(nic);
     //   user.setProfileImage(profileImageBase64);

        return userRepository.save(user);
    }

    @Override
    public User updateOwnerProfile(String email, String name, String nic, String profileImageBase64, String accountHolderName, String bankName, long accountNumber) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found with email: " + email);
        }
        User user = userOpt.get();

        if (user.getRole() != UserRole.OWNER) {
            throw new RuntimeException("User is not an OWNER");
        }

        user.setName(name);
        user.setNic(nic);
       // user.setProfileImage(profileImageBase64);

        BankAccount account = new BankAccount();
        account.setUser(user);
        account.setAccountName(accountHolderName);
        account.setBankName(bankName);
        account.setAccountNumber(accountNumber);

        user.getBankAccounts().add(account);

        return userRepository.save(user);

    }


}
