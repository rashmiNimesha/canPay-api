package com.canpay.api.repository.dashboard;

import com.canpay.api.entity.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DBankAccountRepository extends JpaRepository<BankAccount, Long> {
}
