package com.canpay.api.service.implementation;

import com.canpay.api.entity.*;
import com.canpay.api.entity.User.UserRole;
import com.canpay.api.repository.UserRepository;
import com.canpay.api.repository.TransactionRepository;
import com.canpay.api.service.WalletService;
import com.canpay.api.util.WalletNumberGenerator;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

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
        PassengerWallet passengerWallet = user.getPassengerWallet();
        if (passengerWallet == null) {
            passengerWallet = new PassengerWallet(user, WalletNumberGenerator.generateWalletNumber(user.getId()));
            passengerWallet.setBalance(BigDecimal.ZERO);
            user.setPassengerWallet(passengerWallet);
        }
        passengerWallet.setBalance(passengerWallet.getBalance().add(amountDecimal));
        logger.info("Passenger wallet recharged for email: {}, walletNumber: {}, amount: {}", email, passengerWallet.getWalletNumber(), amount);

        User updatedUser = userRepository.save(user);
        logger.debug("Passenger wallet recharge completed for email: {}, walletNumber: {}, new balance: {}",
                email, passengerWallet.getWalletNumber(), passengerWallet.getBalance());
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

        PassengerWallet passengerWallet = user.getPassengerWallet();
        if (passengerWallet == null) {
            logger.debug("No passenger wallet found for email: {}", email);
            return 0.0;
        }
        return passengerWallet.getBalance().doubleValue();
    }


//    @Transactional
//    @Override
//    public User rechargeBusWallet(String email, double amount, UUID busId) {
//        logger.debug("Recharging bus wallet for email: {}, amount: {}, busId: {}", email, amount, busId);
//
//        User user = userRepository.findByEmailAndRole(email, UserRole.OWNER)
//                .orElseThrow(() -> {
//                    logger.error("User not found for email: {} and role: OWNER", email);
//                    return new RuntimeException("User not found for email: " + email + " and role: OWNER");
//                });
//
//        Bus bus = busRepository.findById(busId)
//                .orElseThrow(() -> {
//                    logger.error("Bus not found for ID: {}", busId);
//                    return new RuntimeException("Bus not found for ID: " + busId);
//                });
//
//        if (!bus.getOwner().getId().equals(user.getId())) {
//            logger.error("Bus {} does not belong to user {}", busId, email);
//            throw new IllegalArgumentException("Bus does not belong to this user");
//        }
//
//        BigDecimal amountDecimal = BigDecimal.valueOf(amount);
//        BusWallet busWallet = bus.getBusWallet();
//        if (busWallet == null) {
//            busWallet = new BusWallet(bus, WalletNumberGenerator.generateBusWalletNumber(bus.getId()));
//            busWallet.setBalance(BigDecimal.ZERO);
//            bus.setBusWallet(busWallet);
//        }
//        busWallet.setBalance(busWallet.getBalance().add(amountDecimal));
//        logger.info("Bus wallet recharged for email: {}, busId: {}, walletNumber: {}, amount: {}",
//                email, busId, busWallet.getWalletNumber(), amount);
//
//        User updatedUser = userRepository.save(user);
//        logger.debug("Bus wallet recharge completed for email: {}, busId: {}, walletNumber: {}, new balance: {}",
//                email, busId, busWallet.getWalletNumber(), busWallet.getBalance());
//        return updatedUser;
//    }
//

//
//
//    @Override
//    public double getBusWalletBalance(String email, UUID busId) {
//        logger.debug("Fetching bus wallet balance for email: {}, busId: {}", email, busId);
//
//        User user = userRepository.findByEmailAndRole(email, UserRole.OWNER)
//                .orElseThrow(() -> {
//                    logger.error("User not found for email: {} and role: OWNER", email);
//                    return new RuntimeException("User not found for email: " + email + " and role: OWNER");
//                });
//
//        Bus bus = busRepository.findById(busId)
//                .orElseThrow(() -> {
//                    logger.error("Bus not found for ID: {}", busId);
//                    return new RuntimeException("Bus not found for ID: " + busId);
//                });
//
//        if (!bus.getOwner().getId().equals(user.getId())) {
//            logger.error("Bus {} does not belong to user {}", busId, email);
//            throw new IllegalArgumentException("Bus does not belong to this user");
//        }
//
//        BusWallet busWallet = bus.getBusWallet();
//        if (busWallet == null) {
//            logger.debug("No bus wallet found for email: {}, busId: {}", email, busId);
//            return 0.0;
//        }
//        return busWallet.getBalance().doubleValue();
//    }


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
