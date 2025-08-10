package com.canpay.api.repository.dashboard;

import com.canpay.api.dto.dashboard.transactions.BusTransactionDto;
import com.canpay.api.entity.Transaction;
import com.canpay.api.entity.Transaction.TransactionType;
import com.canpay.api.entity.Transaction.TransactionStatus;
import com.canpay.api.entity.User;
import com.canpay.api.entity.Bus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.lang.NonNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Dashboard repository for managing Transaction entities.
 * Provides query methods for retrieving and counting transactions by type and
 * associated entities.
 */
@Repository
public interface DTransactionRepository extends JpaRepository<Transaction, UUID> {

    /** Find transaction by its UUID. */
    @Override
    @NonNull
    Optional<Transaction> findById(@NonNull UUID id);

    /** Find transactions by type */
    List<Transaction> findByType(TransactionType type);

    /** Find transactions by status */
    List<Transaction> findByStatus(TransactionStatus status);

    /** Find transactions by passenger */
    List<Transaction> findByPassenger(User passenger);

    /** Find transactions by passenger ID */
    List<Transaction> findByPassenger_Id(UUID passengerId);

    /** Find transactions by bus */
    List<Transaction> findByBus(Bus bus);

    /** Find transactions by bus ID */
    List<Transaction> findByBus_Id(UUID busId);

    /** Find transactions by operator */
    List<Transaction> findByOperator(User operator);

    /** Find transactions by operator ID */
    List<Transaction> findByOperator_Id(UUID operatorId);

    /** Find transactions by owner */
    List<Transaction> findByOwner(User owner);

    /** Find transactions by owner ID */
    List<Transaction> findByOwner_Id(UUID ownerId);

    /** Find transactions by happened at date range */
    List<Transaction> findByHappenedAtBetween(LocalDateTime start, LocalDateTime end);

    /** Find transactions by created at date range */
    List<Transaction> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    /** Find transactions by amount range */
    List<Transaction> findByAmountBetween(BigDecimal minAmount, BigDecimal maxAmount);

    // RECHARGE specific queries (passenger - bank to passenger wallet)
    /** Find recharge transactions by passenger */
    @Query("SELECT t FROM Transaction t WHERE t.type = 'RECHARGE' AND t.passenger = :passenger ORDER BY t.happenedAt DESC")
    List<Transaction> findRechargeTransactionsByPassenger(@Param("passenger") User passenger);

    /** Find recharge transactions by passenger ID */
    @Query("SELECT t FROM Transaction t WHERE t.type = 'RECHARGE' AND t.passenger.id = :passengerId ORDER BY t.happenedAt DESC")
    List<Transaction> findRechargeTransactionsByPassengerId(@Param("passengerId") UUID passengerId);

    /** Find recharge transactions with bank account details */
    @Query("SELECT t FROM Transaction t WHERE t.type = 'RECHARGE' AND t.fromBankAccount IS NOT NULL AND t.toWallet IS NOT NULL ORDER BY t.happenedAt DESC")
    List<Transaction> findRechargeTransactionsWithBankDetails();

    // WITHDRAWAL specific queries (owner - owner wallet to bank or bus wallet to
    // owner wallet)
    /** Find withdrawal transactions by owner */
    @Query("SELECT t FROM Transaction t WHERE t.type = 'WITHDRAWAL' AND t.owner = :owner ORDER BY t.happenedAt DESC")
    List<Transaction> findWithdrawalTransactionsByOwner(@Param("owner") User owner);

    /** Find withdrawal transactions by owner ID */
    @Query("SELECT t FROM Transaction t WHERE t.type = 'WITHDRAWAL' AND t.owner.id = :ownerId ORDER BY t.happenedAt DESC")
    List<Transaction> findWithdrawalTransactionsByOwnerId(@Param("ownerId") UUID ownerId);

    /** Find owner wallet to bank withdrawals */
    @Query("SELECT t FROM Transaction t WHERE t.type = 'WITHDRAWAL' AND t.fromWallet IS NOT NULL AND t.toBankAccount IS NOT NULL AND t.owner IS NOT NULL ORDER BY t.happenedAt DESC")
    List<Transaction> findOwnerWalletToBankWithdrawals();

    /** Find bus wallet to owner wallet withdrawals */
    @Query("SELECT t FROM Transaction t WHERE t.type = 'WITHDRAWAL' AND t.fromWallet IS NOT NULL AND t.toWallet IS NOT NULL AND t.owner IS NOT NULL AND t.bus IS NOT NULL ORDER BY t.happenedAt DESC")
    List<Transaction> findBusToOwnerWalletWithdrawals();

    // PAYMENT specific queries (passenger, owner, operator, bus - from passenger
    // wallet to bus wallet)
    /** Find payment transactions with full details */
    @Query("SELECT t FROM Transaction t WHERE t.type = 'PAYMENT' AND t.passenger IS NOT NULL AND t.bus IS NOT NULL AND t.operator IS NOT NULL ORDER BY t.happenedAt DESC")
    List<Transaction> findPaymentTransactionsWithFullDetails();

    /** Find payment transactions by passenger */
    @Query("SELECT t FROM Transaction t WHERE t.type = 'PAYMENT' AND t.passenger = :passenger ORDER BY t.happenedAt DESC")
    List<Transaction> findPaymentTransactionsByPassenger(@Param("passenger") User passenger);

    /** Find payment transactions by bus */
    @Query("SELECT t FROM Transaction t WHERE t.type = 'PAYMENT' AND t.bus = :bus ORDER BY t.happenedAt DESC")
    List<Transaction> findPaymentTransactionsByBus(@Param("bus") Bus bus);

    /** Find payment transactions by operator */
    @Query("SELECT t FROM Transaction t WHERE t.type = 'PAYMENT' AND t.operator = :operator ORDER BY t.happenedAt DESC")
    List<Transaction> findPaymentTransactionsByOperator(@Param("operator") User operator);

    /** Find payment transactions by bus owner */
    @Query("SELECT t FROM Transaction t WHERE t.type = 'PAYMENT' AND t.bus.owner = :owner ORDER BY t.happenedAt DESC")
    List<Transaction> findPaymentTransactionsByBusOwner(@Param("owner") User owner);

    // Combined queries
    /** Find transactions by type and status */
    List<Transaction> findByTypeAndStatus(TransactionType type, TransactionStatus status);

    /** Find transactions by type and passenger */
    List<Transaction> findByTypeAndPassenger(TransactionType type, User passenger);

    /** Find transactions by type and date range */
    List<Transaction> findByTypeAndHappenedAtBetween(TransactionType type, LocalDateTime start, LocalDateTime end);

    /** Find recent transactions by passenger (limit 10) */
    @Query("SELECT t FROM Transaction t WHERE t.passenger = :passenger ORDER BY t.happenedAt DESC LIMIT 10")
    List<Transaction> findRecentTransactionsByPassenger(@Param("passenger") User passenger);

    /** Find recent transactions by bus (limit 10) */
    @Query("SELECT t FROM Transaction t WHERE t.bus = :bus ORDER BY t.happenedAt DESC LIMIT 10")
    List<Transaction> findRecentTransactionsByBus(@Param("bus") Bus bus);

    // Count queries
    /** Count transactions by type */
    long countByType(TransactionType type);

    /** Count transactions by status */
    long countByStatus(TransactionStatus status);

    /** Count transactions by type and status */
    long countByTypeAndStatus(TransactionType type, TransactionStatus status);

    /** Count transactions by passenger */
    long countByPassenger(User passenger);

    /** Count transactions by passenger ID */
    long countByPassenger_Id(UUID passengerId);

    /** Count transactions by bus */
    long countByBus(Bus bus);

    /** Count transactions by bus ID */
    long countByBus_Id(UUID busId);

    /** Count transactions by operator */
    long countByOperator(User operator);

    /** Count transactions by operator ID */
    long countByOperator_Id(UUID operatorId);

    /** Count transactions by owner */
    long countByOwner(User owner);

    /** Count transactions by owner ID */
    long countByOwner_Id(UUID ownerId);

    /** Count transactions by date range */
    long countByHappenedAtBetween(LocalDateTime start, LocalDateTime end);

    /** Count transactions by type and date range */
    long countByTypeAndHappenedAtBetween(TransactionType type, LocalDateTime start, LocalDateTime end);

    // Sum queries for amounts
    /** Sum transaction amounts by type */
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.type = :type")
    BigDecimal sumAmountByType(@Param("type") TransactionType type);

    /** Sum transaction amounts by type and status */
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.type = :type AND t.status = :status")
    BigDecimal sumAmountByTypeAndStatus(@Param("type") TransactionType type, @Param("status") TransactionStatus status);

    /** Sum transaction amounts by passenger */
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.passenger = :passenger")
    BigDecimal sumAmountByPassenger(@Param("passenger") User passenger);

    /** Sum transaction amounts by bus */
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.bus = :bus")
    BigDecimal sumAmountByBus(@Param("bus") Bus bus);

    /** Sum transaction amounts by date range */
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.happenedAt BETWEEN :start AND :end")
    BigDecimal sumAmountByDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    // Wallet specific queries
    /** Find transactions by from wallet ID */
    List<Transaction> findByFromWallet_Id(UUID walletId);

    /** Find transactions by to wallet ID */
    List<Transaction> findByToWallet_Id(UUID walletId);

    /** Find transactions by bank account ID (from) */
    List<Transaction> findByFromBankAccount_Id(UUID bankAccountId);

    /** Find transactions by bank account ID (to) */
    List<Transaction> findByToBankAccount_Id(UUID bankAccountId);

    // Delete operations
    /** Delete transactions by passenger ID */
    void deleteByPassenger_Id(UUID passengerId);

    /** Delete transactions by bus ID */
    void deleteByBus_Id(UUID busId);

    /** Delete transactions by operator ID */
    void deleteByOperator_Id(UUID operatorId);

    /** Delete transactions by owner ID */
    void deleteByOwner_Id(UUID ownerId);

    List<BusTransactionDto> findTop10ByOperatorIdAndBusIdOrderByHappenedAtDesc(UUID operatorId, UUID busId);

    @Query("SELECT t FROM Transaction t " +
            "LEFT JOIN t.bus b " +
            "LEFT JOIN t.operator o " +
            "LEFT JOIN t.owner ow " +
            "WHERE t.passenger = :passenger " +
            "ORDER BY t.happenedAt DESC")
    List<Transaction> findTop10ByPassengerOrderByHappenedAtDesc(User passenger);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.bus.id = :busId AND t.type = com.canpay.api.entity.Transaction.TransactionType.PAYMENT AND t.status = com.canpay.api.entity.Transaction.TransactionStatus.APPROVED")
    BigDecimal sumPaymentsForBus(UUID busId);
}
