package com.canpay.api.service.implementation;


import com.canpay.api.entity.BankAccount;
import com.canpay.api.entity.User;
import com.canpay.api.repository.bankaccount.BankAccountRepository;
import com.canpay.api.repository.user.UserRepository;
import com.canpay.api.service.BankAccountService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BankAccountServiceImpl implements BankAccountService {
    private final UserRepository userRepository;
    private final BankAccountRepository bankAccountRepository;

    public BankAccountServiceImpl(UserRepository userRepository, BankAccountRepository bankAccountRepository) {
        this.userRepository = userRepository;
        this.bankAccountRepository = bankAccountRepository;
    }

    @Override
    public List<BankAccount> getAccountsByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return bankAccountRepository.findByUser(user);
    }
}
