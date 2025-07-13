package com.canpay.api.service.implementation;

import com.canpay.api.entity.Transaction;
import com.canpay.api.entity.User;
import com.canpay.api.repository.TransactionRepository;
import com.canpay.api.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final Logger logger = LoggerFactory.getLogger(TransactionService.class);

    public TransactionService(TransactionRepository transactionRepository, UserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }

    public List<Transaction> getRecentTransactions(String passengerEmail) {
        User passenger = userRepository.findByEmail(passengerEmail)
                .orElseThrow(() -> {
                    logger.warn("Passenger not found: {}", passengerEmail);
                    return new RuntimeException("Passenger not found");
                });
        List<Transaction> transactions = transactionRepository.findTop10ByPassengerOrderByHappenedAtDesc(passenger);
        logger.info("Fetched {} recent transactions for passenger: {}", transactions.size(), passengerEmail);
        return transactions;
    }
}