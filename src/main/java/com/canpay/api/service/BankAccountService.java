package com.canpay.api.service;

import com.canpay.api.dto.BankAccountDto;
import com.canpay.api.entity.User;

import java.util.List;

public interface BankAccountService {
    List<BankAccountDto> getAccountsByEmail(String email);

    User getUserByEmailAndRole(String email);
}