package com.canpay.api.repository;

import com.canpay.api.entity.Transaction;
import com.canpay.api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    @Query("SELECT t FROM Transaction t " +
            "LEFT JOIN t.bus b " +
            "LEFT JOIN t.operator o " +
            "LEFT JOIN t.owner ow " +
            "WHERE t.passenger = :passenger " +
            "ORDER BY t.happenedAt DESC")
    List<Transaction> findTop10ByPassengerOrderByHappenedAtDesc(User passenger);
}