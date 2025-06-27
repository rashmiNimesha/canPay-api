package com.canpay.api.repository;

import com.canpay.api.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface TransactionRepository
    extends JpaRepository<Transaction, UUID> {
    // add custom queries here if needed
}
