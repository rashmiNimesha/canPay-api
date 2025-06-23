package com.canpay.api.service.implementation;

import com.canpay.api.entity.BankAccount;
import com.canpay.api.entity.User;
import com.canpay.api.repository.user.UserRepository;
import com.canpay.api.service.UserSevice;
import org.springframework.stereotype.Service;

import java.util.List;
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
    public User registerWithEmail(String email) {
        User user = new User();
        user.setEmail(email);
        user.setRole("PASSENGER");
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
            bankAccount.setAccountHolderName(accName);
            bankAccount.setAccountNumber(accNo);
            bankAccount.setBankName(bank);
            bankAccount.setUser(user);
            user.getBankAccounts().add(bankAccount);
        }

        return userRepository.save(user);
    }


}
