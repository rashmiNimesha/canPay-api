package com.canpay.api.service;

import com.canpay.api.entity.BankAccount;

import java.util.List;

public interface BankAccountService {
    List<BankAccount> getAccountsByEmail(String email);
}