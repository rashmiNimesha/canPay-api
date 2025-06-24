package com.canpay.api.service;

import com.canpay.api.entity.User;
import org.springframework.stereotype.Service;

@Service
public interface WalletService {

     User rechargeWallet(String email, double amount);

    }
