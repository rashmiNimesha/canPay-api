package com.canpay.api.repository.bankaccount;

import com.canpay.api.entity.BankAccount;
import com.canpay.api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {
    List<BankAccount> findByUser(User user);
}