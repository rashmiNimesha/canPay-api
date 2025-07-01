package com.canpay.api.service.implementation;

import com.canpay.api.dto.BankAccountDto;
import com.canpay.api.entity.BankAccount;
import com.canpay.api.entity.User;
import com.canpay.api.repository.UserRepository;
import com.canpay.api.repository.bankaccount.BankAccountRepository;
import com.canpay.api.service.BankAccountService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.canpay.api.service.implementation.WalletServiceImpl.logger;

@Service
public class BankAccountServiceImpl implements BankAccountService {
    private final UserRepository userRepository;
    private final BankAccountRepository bankAccountRepository;

    public BankAccountServiceImpl(UserRepository userRepository, BankAccountRepository bankAccountRepository) {
        this.userRepository = userRepository;
        this.bankAccountRepository = bankAccountRepository;
    }
    @Override
    public User getUserByEmailAndRole(String email) {
        logger.debug("Fetching user by email: {} and role: PASSENGER", email);
        return userRepository.findByEmailAndRole(email, User.UserRole.PASSENGER)
                .orElse(null);
    }

    @Override
    public List<BankAccountDto> getAccountsByEmail(String email) {
        logger.debug("Fetching bank accounts for email: {}", email);

        User user = userRepository.findByEmailAndRole(email, User.UserRole.PASSENGER)
                .orElseThrow(() -> {
                    logger.error("User not found for email: {} and role: PASSENGER", email);
                    return new RuntimeException("User not found for email: " + email + " and role: PASSENGER");
                });

        List<BankAccount> bankAccounts = user.getBankAccounts();
        if (bankAccounts == null || bankAccounts.isEmpty()) {
            logger.warn("No bank accounts found for email: {}", email);
            return List.of();
        }

        return bankAccounts.stream()
                .map(account -> new BankAccountDto(
                        account.getBankName(),
                        account.getAccountNumber(),
                        account.getAccountName(),
                        account.isDefault()
                ))
                .collect(Collectors.toList());
    }
}
