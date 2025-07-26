package com.canpay.api.service.implementation;

import com.canpay.api.dto.UserWalletBalanceDto;
import com.canpay.api.entity.*;
import com.canpay.api.entity.User.UserRole;
import com.canpay.api.entity.Wallet.WalletType;
import com.canpay.api.lib.Utils;
import com.canpay.api.repository.UserRepository;
import com.canpay.api.repository.TransactionRepository;
import com.canpay.api.repository.bankaccount.BankAccountRepository;
import com.canpay.api.repository.dashboard.DWalletRepository;
import com.canpay.api.service.WalletService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class WalletServiceImpl implements WalletService {

    final static Logger logger = LoggerFactory.getLogger(WalletServiceImpl.class);
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final DWalletRepository walletRepository;
    private final BankAccountRepository bankAccountRepository;

    public WalletServiceImpl(UserRepository userRepository, TransactionRepository transactionRepository, DWalletRepository walletRepository, BankAccountRepository bankAccountRepository) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
        this.walletRepository = walletRepository;
        this.bankAccountRepository = bankAccountRepository;
    }

//    @Transactional
//    public UserWalletBalanceDto rechargePassengerWallet(String email, double amount) {
//        logger.debug("Recharging passenger wallet for email: {}, amount: {}", email, amount);
//
//        User user = userRepository.findByEmailAndRole(email, UserRole.PASSENGER)
//                .orElseThrow(() -> {
//                    logger.error("User not found for email: {} and role: PASSENGER", email);
//                    return new RuntimeException("User not found for email: " + email);
//                });
//
//        BigDecimal amountDecimal = BigDecimal.valueOf(amount);
//        Wallet wallet;
//
//        Optional<Wallet> existingWallet = walletRepository.findByUser_IdAndType(user.getId(), WalletType.PASSENGER);
//        if (existingWallet.isPresent()) {
//            wallet = existingWallet.get();
//            wallet.setBalance(wallet.getBalance().add(amountDecimal));
//            logger.info("Recharging existing wallet for email: {}, walletNumber: {}, amount: {}, new balance: {}",
//                    email, wallet.getWalletNumber(), amount, wallet.getBalance());
//        } else {
//            wallet = new Wallet(user, Utils.generateUniqueWalletNumber(walletRepository), WalletType.PASSENGER);
//            wallet.setBalance(amountDecimal);
//            logger.info("Creating new wallet for email: {}, walletNumber: {}, initial balance: {}",
//                    email, wallet.getWalletNumber(), amount);
//        }
//
//        walletRepository.save(wallet);
//
//        return new UserWalletBalanceDto(
//                wallet.getWalletNumber(),
//                wallet.getBalance().doubleValue(),
//                user.getName() != null ? user.getName() : "N/A"
//        );
//    }

//    @Transactional
//    public UserWalletBalanceDto rechargePassengerWallet(String email, double amount) {
//        logger.debug("Recharging passenger wallet for email: {}, amount: {}", email, amount);
//
//        User user = userRepository.findByEmailAndRole(email, UserRole.PASSENGER)
//                .orElseThrow(() -> {
//                    logger.error("User not found for email: {} and role: PASSENGER", email);
//                    return new RuntimeException("User not found for email: " + email);
//                });
//
//        BankAccount bankAccount = bankAccountRepository.findByUserAndDefaultTrue(user)
//                .orElseThrow(() -> {
//                    logger.error("Default bank account not found for user: {}", email);
//                    return new RuntimeException("Default bank account not found for user: " + email);
//                });
//
//        BigDecimal amountDecimal = BigDecimal.valueOf(amount);
//        Wallet wallet;
//
//        Optional<Wallet> existingWallet = walletRepository.findByUser_IdAndType(user.getId(), WalletType.PASSENGER);
//        if (existingWallet.isPresent()) {
//            wallet = existingWallet.get();
//            wallet.setBalance(wallet.getBalance().add(amountDecimal));
//            logger.info("Recharging existing wallet for email: {}, walletNumber: {}, amount: {}, new balance: {}",
//                    email, wallet.getWalletNumber(), amount, wallet.getBalance());
//        } else {
//            wallet = new Wallet(user, Utils.generateUniqueWalletNumber(walletRepository), WalletType.PASSENGER);
//            wallet.setBalance(amountDecimal);
//            logger.info("Creating new wallet for email: {}, walletNumber: {}, initial balance: {}",
//                    email, wallet.getWalletNumber(), amount);
//        }
//
//        // Create transaction record
//        Transaction transaction = new Transaction(amountDecimal, Transaction.TransactionType.RECHARGE, user);
//        transaction.setStatus(Transaction.TransactionStatus.APPROVED);
//        transaction.setNote("Wallet recharge from default bank account: " + bankAccount.getAccountNumber());
//        transaction.setFromBankAccount(bankAccount);
//        transaction.setToWallet(wallet);
//        transaction.setHappenedAt(java.time.LocalDateTime.now());
//
//        // Save wallet and transaction
//        walletRepository.save(wallet);
//        transactionRepository.save(transaction);
//
//        logger.info("Transaction recorded for email: {}, amount: {}, fromBankAccount: {}, toWallet: {}",
//                email, amount, bankAccount.getAccountName(), wallet.getWalletNumber());
//
//        return new UserWalletBalanceDto(
//                wallet.getWalletNumber(),
//                wallet.getBalance().doubleValue(),
//                user.getName() != null ? user.getName() : "N/A"
//        );
//    }


    @Transactional
    public UserWalletBalanceDto rechargePassengerWallet(String email, double amount) {
        logger.debug("Recharging passenger wallet for email: {}, amount: {}", email, amount);

        User user = userRepository.findByEmailAndRole(email, UserRole.PASSENGER)
                .orElseThrow(() -> {
                    logger.error("User not found for email: {} and role: PASSENGER", email);
                    return new RuntimeException("User not found for email: " + email);
                });

        Optional<BankAccount> bankAccountOpt = bankAccountRepository.findByUserAndIsDefaultTrue(user);
        if (bankAccountOpt.isEmpty()) {
            logger.error("No default bank account found for user: {}", email);
            throw new RuntimeException("No default bank account set for user: " + email);
        }
        BankAccount bankAccount = bankAccountOpt.get();

        BigDecimal amountDecimal = BigDecimal.valueOf(amount);
        Wallet wallet;

        Optional<Wallet> existingWallet = walletRepository.findByUser_IdAndType(user.getId(), WalletType.PASSENGER);
        if (existingWallet.isPresent()) {
            wallet = existingWallet.get();
            wallet.setBalance(wallet.getBalance().add(amountDecimal));
            logger.info("Recharging existing wallet for email: {}, walletNumber: {}, amount: {}, new balance: {}",
                    email, wallet.getWalletNumber(), amount, wallet.getBalance());
        } else {
            wallet = new Wallet(user, Utils.generateUniqueWalletNumber(walletRepository), WalletType.PASSENGER);
            wallet.setBalance(amountDecimal);
            logger.info("Creating new wallet for email: {}, walletNumber: {}, initial balance: {}",
                    email, wallet.getWalletNumber(), amount);
        }

        // Create transaction record
        Transaction transaction = new Transaction(amountDecimal, Transaction.TransactionType.RECHARGE, user);
        transaction.setStatus(Transaction.TransactionStatus.APPROVED);
        transaction.setNote("Wallet recharge from default bank account: " + bankAccount.getAccountNumber());
        transaction.setFromBankAccount(bankAccount);
        transaction.setToWallet(wallet);
        transaction.setHappenedAt(java.time.LocalDateTime.now());

        // Save wallet and transaction
        walletRepository.save(wallet);
        transactionRepository.save(transaction);

        logger.info("Transaction recorded for email: {}, amount: {}, fromBankAccount: {}, toWallet: {}",
                email, amount, bankAccount.getAccountNumber(), wallet.getWalletNumber());

        return new UserWalletBalanceDto(
                wallet.getWalletNumber(),
                wallet.getBalance().doubleValue(),
                user.getName() != null ? user.getName() : "N/A"
        );
    }



    @Transactional(readOnly = true)
    public UserWalletBalanceDto getPassengerWalletBalanceForDash(String email) {
        logger.debug("Fetching passenger wallet balance for email: {}", email);

        User user = userRepository.findByEmailAndRole(email, UserRole.PASSENGER)
                .orElseThrow(() -> {
                    logger.error("User not found for email: {} and role: PASSENGER", email);
                    return new RuntimeException("User not found for email: " + email);
                });

        Optional<Wallet> walletOpt = walletRepository.findByUser_IdAndType(user.getId(), WalletType.PASSENGER);
        if (walletOpt.isPresent()) {
            Wallet wallet = walletOpt.get();
            return new UserWalletBalanceDto(
                    wallet.getWalletNumber(),
                    wallet.getBalance().doubleValue(),
                    user.getName() != null ? user.getName() : "N/A"
            );
        } else {
            logger.warn("No PASSENGER wallet found for email: {}", email);
            return new UserWalletBalanceDto(null, 0.0, user.getName() != null ? user.getName() : "N/A");
        }
    }

    @Override
    public User getUserByEmailAndRole(String email) {
        logger.debug("Fetching user by email: {} and role: PASSENGER", email);
        return userRepository.findByEmailAndRole(email, UserRole.PASSENGER)
                .orElse(null);
    }

    public List<Transaction> getTransactionHistory(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Return empty list for now - adjust based on your repository method
        return transactionRepository.findAll(); // Replace with correct method
    }
}
