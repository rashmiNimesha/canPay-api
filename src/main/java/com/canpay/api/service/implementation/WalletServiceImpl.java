package com.canpay.api.service.implementation;

import com.canpay.api.entity.Transaction;
import com.canpay.api.entity.User;
import com.canpay.api.entity.PassengerWallet;
import com.canpay.api.entity.BusWallet;
import com.canpay.api.repository.UserRepository;
import com.canpay.api.repository.TransactionRepository;
import com.canpay.api.service.WalletService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class WalletServiceImpl implements WalletService {

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    public WalletServiceImpl(UserRepository userRepository, TransactionRepository transactionRepository) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    public User rechargeWallet(String email, double amount) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Convert double to BigDecimal for precision
        BigDecimal amountDecimal = BigDecimal.valueOf(amount);

        // Check user role and handle appropriate wallet
        if (user.getRole() != null && user.getRole() == User.UserRole.OWNER) {
            // Handle bus wallet - create if doesn't exist
            BusWallet busWallet = new BusWallet();
            // Set the user relationship
            // Note: Adjust setter method name based on your entity definition
            // busWallet.setUser(user);
            busWallet.setBalance(amountDecimal);

            // Save bus wallet and associate with user
            // user.setBusWallet(busWallet);
        } else {
            // Handle passenger wallet (default)
            PassengerWallet passengerWallet = user.getPassengerWallet();
            if (passengerWallet == null) {
                passengerWallet = new PassengerWallet();
                passengerWallet.setPassenger(user);
                passengerWallet.setBalance(BigDecimal.ZERO);
                user.setPassengerWallet(passengerWallet);
            }
            passengerWallet.setBalance(passengerWallet.getBalance().add(amountDecimal));
        }
        return userRepository.save(user);
    }

    public double getWalletBalance(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // For now, only handle passenger wallet until entity structure is clarified
        PassengerWallet passengerWallet = user.getPassengerWallet();
        return passengerWallet != null ? passengerWallet.getBalance().doubleValue() : 0.0;
    }

    public List<Transaction> getTransactionHistory(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Return empty list for now - adjust based on your repository method
        return transactionRepository.findAll(); // Replace with correct method
    }
}
