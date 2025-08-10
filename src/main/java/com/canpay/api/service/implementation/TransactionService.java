package com.canpay.api.service.implementation;

import com.canpay.api.dto.dashboard.transactions.BusTransactionDto;
import com.canpay.api.dto.dashboard.transactions.RechargeTransactionDto;
import com.canpay.api.entity.Transaction;
import com.canpay.api.entity.User;
import com.canpay.api.repository.TransactionRepository;
import com.canpay.api.repository.UserRepository;
import com.canpay.api.repository.dashboard.DTransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TransactionService {
    private final DTransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final Logger logger = LoggerFactory.getLogger(TransactionService.class);

    public TransactionService(DTransactionRepository transactionRepository, UserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }

    public List<Transaction> getRecentTransactions(UUID passengerID) {
        User passenger = userRepository.findById(passengerID)
                .orElseThrow(() -> {
                    logger.warn("Passenger not found: {}", passengerID);
                    return new RuntimeException("Passenger not found");
                });
        List<Transaction> transactions = transactionRepository.findTop10ByPassengerOrderByHappenedAtDesc(passenger);
        logger.info("Fetched {} recent transactions for passenger: {}", transactions.size(), passengerID);
        return transactions;
    }

    /**
     * Gets recharge transactions by passenger ID as DTOs.
     */
    public List<RechargeTransactionDto> getRechargeTransactionsByPassengerId(UUID passengerId) {
        List<Transaction> transactions = transactionRepository.findRechargeTransactionsByPassengerId(passengerId);
        return transactions.stream()
                .map(this::convertToRechargeDto)
                .collect(Collectors.toList());
    }

    private RechargeTransactionDto convertToRechargeDto(Transaction transaction) {
        if (transaction.getType() != Transaction.TransactionType.RECHARGE) {
            throw new IllegalArgumentException("Transaction is not a recharge transaction");
        }

        return new RechargeTransactionDto(
                transaction.getId(),
                transaction.getAmount(),
                transaction.getHappenedAt(),
                transaction.getStatus(),
                transaction.getNote(),
                // Passenger details
                transaction.getPassenger() != null ? transaction.getPassenger().getId() : null,
                transaction.getPassenger() != null ? transaction.getPassenger().getName() : null,
                transaction.getPassenger() != null ? transaction.getPassenger().getEmail() : null,
                // From bank account details
                transaction.getFromBankAccount() != null ? transaction.getFromBankAccount().getId() : null,
                transaction.getFromBankAccount() != null ? transaction.getFromBankAccount().getBankName() : null,
                transaction.getFromBankAccount() != null
                        ? String.valueOf(transaction.getFromBankAccount().getAccountNumber())
                        : null,
                // To wallet details
                transaction.getToWallet() != null ? transaction.getToWallet().getId() : null,
                transaction.getToWallet() != null ? transaction.getToWallet().getWalletNumber() : null,
                transaction.getToWallet() != null ? transaction.getToWallet().getBalance() : null);
    }

    public BigDecimal sumPaymentsForBus(java.util.UUID busId) {
        return transactionRepository.sumPaymentsForBus(busId);
    }

}