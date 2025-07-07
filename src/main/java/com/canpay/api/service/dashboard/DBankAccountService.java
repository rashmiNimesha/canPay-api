package com.canpay.api.service.dashboard;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.canpay.api.dto.Dashboard.DBankAccountDto;
import com.canpay.api.entity.BankAccount;
import com.canpay.api.entity.User;
import com.canpay.api.repository.dashboard.DBankAccountRepository;

/**
 * Service for managing BankAccount entities in the dashboard context.
 * Handles bank account creation, replacement, and management.
 */
@Service
public class DBankAccountService {

    private final DBankAccountRepository bankAccountRepository;

    @Autowired
    public DBankAccountService(DBankAccountRepository bankAccountRepository) {
        this.bankAccountRepository = bankAccountRepository;
    }

    /**
     * Creates bank accounts from DTOs and associates them with the user.
     */
    @Transactional
    public List<BankAccount> createBankAccounts(User user, List<DBankAccountDto> bankAccountDtos) {
        List<BankAccount> bankAccounts = new ArrayList<>();

        if (bankAccountDtos != null && !bankAccountDtos.isEmpty()) {
            for (DBankAccountDto bankDto : bankAccountDtos) {
                // Validate required bank account fields
                if (bankDto.getBankName() != null && !bankDto.getBankName().isBlank() &&
                        bankDto.getAccountNumber() != null && !bankDto.getAccountNumber().isBlank() &&
                        bankDto.getAccountName() != null && !bankDto.getAccountName().isBlank()) {

                    BankAccount bankAccount = new BankAccount();
                    bankAccount.setAccountName(bankDto.getAccountName());
                    bankAccount.setAccountNumber(Long.parseLong(bankDto.getAccountNumber()));
                    bankAccount.setBankName(bankDto.getBankName());
                    bankAccount.setDefault(bankDto.isDefault());
                    bankAccount.setUser(user);
                    bankAccounts.add(bankAccount);
                }
            }
        }

        // Save all new bank accounts
        if (!bankAccounts.isEmpty()) {
            bankAccountRepository.saveAll(bankAccounts);
        }

        return bankAccounts;
    }

    /**
     * Replaces all bank accounts for a user with new ones in a transactional
     * manner.
     * This ensures atomicity - either all operations succeed or all fail.
     */
    @Transactional
    public void replaceBankAccounts(User user, List<DBankAccountDto> bankAccountDtos) {
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

    /**
     * Gets all bank accounts for a user as DTOs.
     */
    public List<DBankAccountDto> getBankAccountsByUserId(UUID userId) {
        return bankAccountRepository.findByUserId(userId).stream()
                .map(DBankAccountDto::new)
                .collect(Collectors.toList());
    }

    /**
     * Gets all bank accounts for a user as entities.
     */
    public List<BankAccount> getBankAccountEntitiesByUserId(UUID userId) {
        return bankAccountRepository.findByUserId(userId);
    }

    /**
     * Deletes all bank accounts for a user.
     */
    @Transactional
    public void deleteBankAccountsByUserId(UUID userId) {
        bankAccountRepository.deleteByUserId(userId);
    }

    /**
     * Gets the count of bank accounts for a user.
     */
    public long getBankAccountCountByUserId(UUID userId) {
        return bankAccountRepository.countByUserId(userId);
    }

    /**
     * Gets the default bank account for a user.
     */
    public BankAccount getDefaultBankAccountByUserId(UUID userId) {
        return bankAccountRepository.findByUserIdAndIsDefaultTrue(userId).orElse(null);
    }
}
