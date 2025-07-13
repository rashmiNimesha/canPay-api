package com.canpay.api.repository.dashboard;

import com.canpay.api.entity.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.lang.NonNull;

import java.util.UUID;
import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

/**
 * Dashboard repository for managing BankAccount entities.
 * Provides query methods for retrieving and counting bank accounts.
 */
@Repository
public interface DBankAccountRepository extends JpaRepository<BankAccount, UUID> {
    /** Find bank account by its UUID. */
    @Override
    @NonNull
    Optional<BankAccount> findById(@NonNull UUID id);

    /** Find bank accounts by exact bank name */
    List<BankAccount> findByBankName(String bankName);

    /** Find bank account by account number */
    Optional<BankAccount> findByAccountNumber(Long accountNumber);

    /** Find bank accounts by exact account name */
    List<BankAccount> findByAccountName(String accountName);

    /** Find bank accounts by default flag */
    List<BankAccount> findByIsDefault(boolean isDefault);

    /** Find bank accounts created within given date range */
    List<BankAccount> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    /** Find bank accounts updated within given date range */
    List<BankAccount> findByUpdatedAtBetween(LocalDateTime start, LocalDateTime end);

    /** Find all bank accounts for a user */
    List<BankAccount> findByUserId(UUID userId);

    /** Count bank accounts by default flag */
    long countByIsDefault(boolean isDefault);

    /** Find bank accounts where bank name contains substring */
    List<BankAccount> findByBankNameContaining(String bankName);

    /** Find bank accounts where account name contains substring */
    List<BankAccount> findByAccountNameContaining(String accountName);

    /** Find the default bank account for a user */
    Optional<BankAccount> findByUserIdAndIsDefaultTrue(UUID userId);

    /** Find bank accounts for a user filtered by default flag */
    List<BankAccount> findByUserIdAndIsDefault(UUID userId, boolean isDefault);

    /** Count total bank accounts for a user */
    long countByUserId(UUID userId);

    /** Count bank accounts for a user filtered by default flag */
    long countByUserIdAndIsDefault(UUID userId, boolean isDefault);

    /** Deletes all bank accounts associated with a specific user ID */
    void deleteByUserId(UUID userId);
}
