package com.canpay.api.repository.wallet;

import com.canpay.api.entity.RechargeTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RechargeTransactionRepository extends JpaRepository<RechargeTransaction, Long> {
}
