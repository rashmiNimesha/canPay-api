package com.canpay.api.service.implementation;

import com.canpay.api.entity.*;
import com.canpay.api.entity.User.UserRole;
import com.canpay.api.entity.Wallet.WalletType;
import com.canpay.api.repository.UserRepository;
import com.canpay.api.repository.TransactionRepository;
import com.canpay.api.service.WalletService;
import com.canpay.api.util.WalletNumberGenerator;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class WalletServiceImpl implements WalletService {

    final static Logger logger = LoggerFactory.getLogger(WalletServiceImpl.class);
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    public WalletServiceImpl(UserRepository userRepository, TransactionRepository transactionRepository) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    @Override
    public User rechargePassengerWallet(String email, double amount) {
        logger.debug("Recharging passenger wallet for email: {}, amount: {}", email, amount);

        User user = userRepository.findByEmailAndRole(email, UserRole.PASSENGER)
                .orElseThrow(() -> {
                    logger.error("User not found for email: {} and role: PASSENGER", email);
                    return new RuntimeException("User not found for email: " + email + " and role: PASSENGER");
                });

        BigDecimal amountDecimal = BigDecimal.valueOf(amount);
        Wallet wallet = user.getWallet();
        if (wallet == null || wallet.getType() != WalletType.PASSENGER) {
            wallet = new Wallet(user, WalletNumberGenerator.generateWalletNumber(user.getId()), WalletType.PASSENGER);
            wallet.setBalance(BigDecimal.ZERO);
            user.setWallet(wallet);
        }
        wallet.setBalance(wallet.getBalance().add(amountDecimal));
        logger.info("Passenger wallet recharged for email: {}, walletNumber: {}, amount: {}", email,
                wallet.getWalletNumber(), amount);

        User updatedUser = userRepository.save(user);
        logger.debug("Passenger wallet recharge completed for email: {}, walletNumber: {}, new balance: {}",
                email, wallet.getWalletNumber(), wallet.getBalance());
        return updatedUser;
    }

    @Override
    public double getPassengerWalletBalance(String email) {
        logger.debug("Fetching passenger wallet balance for email: {}", email);

        User user = userRepository.findByEmailAndRole(email, UserRole.PASSENGER)
                .orElseThrow(() -> {
                    logger.error("User not found for email: {} and role: PASSENGER", email);
                    return new RuntimeException("User not found for email: " + email + " and role: PASSENGER");
                });

        Wallet wallet = user.getWallet();
        if (wallet == null || wallet.getType() != WalletType.PASSENGER) {
            logger.debug("No passenger wallet found for email: {}", email);
            return 0.0;
        }
        return wallet.getBalance().doubleValue();
    }


    public Map<String, Object> getPassengerWalletBalanceForDash(String email) {
        logger.debug("Fetching passenger wallet balance for email: {}", email);

        User user = userRepository.findByEmailAndRole(email, UserRole.PASSENGER)
                .orElseThrow(() -> {
                    logger.error("User not found for email: {} and role: PASSENGER", email);
                    return new RuntimeException("User not found for email: " + email);
                });

        Map<String, Object> result = new HashMap<>();
        result.put("name", user.getName() != null ? user.getName() : "N/A");

        Wallet wallet = user.getWallet();
        if (wallet == null || wallet.getType() != Wallet.WalletType.PASSENGER) {
            logger.debug("No passenger wallet found for email: {}", email);
            result.put("walletNumber", null);
            result.put("balance", 0.0);
        } else {
            result.put("walletNumber", wallet.getId().toString());
            result.put("balance", wallet.getBalance().doubleValue());
        }

        return result;
    }

    @Override
    public User getUserByEmailAndRole(String email) {
        logger.debug("Fetching user by email: {} and role: PASSENGER", email);
        return userRepository.findByEmailAndRole(email, UserRole.PASSENGER)
                .orElse(null);
    }

    // @Transactional
    // @Override
    // public User rechargeBusWallet(String email, double amount, UUID busId) {
    // logger.debug("Recharging bus wallet for email: {}, amount: {}, busId: {}",
    // email, amount, busId);
    //
    // User user = userRepository.findByEmailAndRole(email, UserRole.OWNER)
    // .orElseThrow(() -> {
    // logger.error("User not found for email: {} and role: OWNER", email);
    // return new RuntimeException("User not found for email: " + email + " and
    // role: OWNER");
    // });
    //
    // Bus bus = busRepository.findById(busId)
    // .orElseThrow(() -> {
    // logger.error("Bus not found for ID: {}", busId);
    // return new RuntimeException("Bus not found for ID: " + busId);
    // });
    //
    // if (!bus.getOwner().getId().equals(user.getId())) {
    // logger.error("Bus {} does not belong to user {}", busId, email);
    // throw new IllegalArgumentException("Bus does not belong to this user");
    // }
    //
    // BigDecimal amountDecimal = BigDecimal.valueOf(amount);
    // BusWallet busWallet = bus.getBusWallet();
    // if (busWallet == null) {
    // busWallet = new BusWallet(bus,
    // WalletNumberGenerator.generateBusWalletNumber(bus.getId()));
    // busWallet.setBalance(BigDecimal.ZERO);
    // bus.setBusWallet(busWallet);
    // }
    // busWallet.setBalance(busWallet.getBalance().add(amountDecimal));
    // logger.info("Bus wallet recharged for email: {}, busId: {}, walletNumber: {},
    // amount: {}",
    // email, busId, busWallet.getWalletNumber(), amount);
    //
    // User updatedUser = userRepository.save(user);
    // logger.debug("Bus wallet recharge completed for email: {}, busId: {},
    // walletNumber: {}, new balance: {}",
    // email, busId, busWallet.getWalletNumber(), busWallet.getBalance());
    // return updatedUser;
    // }
    //

    //
    //
    // @Override
    // public double getBusWalletBalance(String email, UUID busId) {
    // logger.debug("Fetching bus wallet balance for email: {}, busId: {}", email,
    // busId);
    //
    // User user = userRepository.findByEmailAndRole(email, UserRole.OWNER)
    // .orElseThrow(() -> {
    // logger.error("User not found for email: {} and role: OWNER", email);
    // return new RuntimeException("User not found for email: " + email + " and
    // role: OWNER");
    // });
    //
    // Bus bus = busRepository.findById(busId)
    // .orElseThrow(() -> {
    // logger.error("Bus not found for ID: {}", busId);
    // return new RuntimeException("Bus not found for ID: " + busId);
    // });
    //
    // if (!bus.getOwner().getId().equals(user.getId())) {
    // logger.error("Bus {} does not belong to user {}", busId, email);
    // throw new IllegalArgumentException("Bus does not belong to this user");
    // }
    //
    // BusWallet busWallet = bus.getBusWallet();
    // if (busWallet == null) {
    // logger.debug("No bus wallet found for email: {}, busId: {}", email, busId);
    // return 0.0;
    // }
    // return busWallet.getBalance().doubleValue();
    // }

    public List<Transaction> getTransactionHistory(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Return empty list for now - adjust based on your repository method
        return transactionRepository.findAll(); // Replace with correct method
    }
}
