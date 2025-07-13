package com.canpay.api.repository.bankaccount;

import com.canpay.api.entity.BankAccount;
import com.canpay.api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {
    List<BankAccount> findByUser(User user);
//    Optional<BankAccount> findByUserAndDefaultTrue(User user);


    Optional<BankAccount> findByUserAndIsDefaultTrue(User user);
}