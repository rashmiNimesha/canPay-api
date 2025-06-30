package com.canpay.api.service;

import com.canpay.api.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public interface WalletService {

    @Transactional
    User rechargePassengerWallet(String email, double amount);

    double getPassengerWalletBalance(String email);

}
