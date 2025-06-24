package com.canpay.api.service.implementation;

import com.canpay.api.entity.RechargeTransaction;
import com.canpay.api.entity.User;
import com.canpay.api.repository.user.UserRepository;
import com.canpay.api.repository.wallet.RechargeTransactionRepository;
import com.canpay.api.service.WalletService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class WalletServiceImpl implements WalletService {

    private final UserRepository userRepository;
    private final RechargeTransactionRepository transactionRepository;

    public WalletServiceImpl(UserRepository userRepository, RechargeTransactionRepository transactionRepository) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    public User rechargeWallet(String email, double amount) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setWalletBalance(user.getWalletBalance() + amount);

        RechargeTransaction transaction = new RechargeTransaction();
        transaction.setAmount(amount);
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setUser(user);

        user.getRechargeHistory().add(transaction);
        transactionRepository.save(transaction);
        return userRepository.save(user);
    }

    public double getWalletBalance(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getWalletBalance();
    }

    public List<RechargeTransaction> getRechargeHistory(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getRechargeHistory();
    }
}
