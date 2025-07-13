package com.canpay.api.service;

import com.canpay.api.dto.UserWalletBalanceDto;
import com.canpay.api.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public interface WalletService {

    @Transactional
    UserWalletBalanceDto rechargePassengerWallet(String email, double amount);

    User getUserByEmailAndRole(String email);
}
