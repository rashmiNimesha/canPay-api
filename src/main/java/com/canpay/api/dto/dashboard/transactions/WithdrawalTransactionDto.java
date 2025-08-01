package com.canpay.api.dto.dashboard.transactions;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.canpay.api.entity.Transaction.TransactionStatus;

/**
 * DTO for withdrawal transaction details.
 * Represents owner wallet to bank or bus wallet to owner wallet transactions.
 */
public class WithdrawalTransactionDto {
    private UUID transactionId;
    private BigDecimal amount;
    private LocalDateTime happenedAt;
    private TransactionStatus status;
    private String note;
    private String withdrawalType; // "OWNER_TO_BANK" or "BUS_TO_OWNER"

    // Owner details
    private UUID ownerId;
    private String ownerName;
    private String ownerEmail;

    // From Wallet details
    private UUID fromWalletId;
    private String fromWalletNumber;
    private BigDecimal fromWalletBalance;
    private String fromWalletType; // "OWNER" or "BUS"

    // To Bank Account details (for owner to bank withdrawals)
    private UUID toBankAccountId;
    private String toBankName;
    private String toAccountNumber;

    // To Wallet details (for bus to owner withdrawals)
    private UUID toWalletId;
    private String toWalletNumber;
    private BigDecimal toWalletBalance;

    // Bus details (for bus to owner withdrawals)
    private UUID busId;
    private String busNumber;
    private String busRoute;

    public WithdrawalTransactionDto() {
    }

    public WithdrawalTransactionDto(UUID transactionId, BigDecimal amount, LocalDateTime happenedAt,
            TransactionStatus status, String note, String withdrawalType, UUID ownerId,
            String ownerName, String ownerEmail, UUID fromWalletId, String fromWalletNumber,
            BigDecimal fromWalletBalance, String fromWalletType, UUID toBankAccountId,
            String toBankName, String toAccountNumber, UUID toWalletId, String toWalletNumber,
            BigDecimal toWalletBalance, UUID busId, String busNumber, String busRoute) {
        this.transactionId = transactionId;
        this.amount = amount;
        this.happenedAt = happenedAt;
        this.status = status;
        this.note = note;
        this.withdrawalType = withdrawalType;
        this.ownerId = ownerId;
        this.ownerName = ownerName;
        this.ownerEmail = ownerEmail;
        this.fromWalletId = fromWalletId;
        this.fromWalletNumber = fromWalletNumber;
        this.fromWalletBalance = fromWalletBalance;
        this.fromWalletType = fromWalletType;
        this.toBankAccountId = toBankAccountId;
        this.toBankName = toBankName;
        this.toAccountNumber = toAccountNumber;
        this.toWalletId = toWalletId;
        this.toWalletNumber = toWalletNumber;
        this.toWalletBalance = toWalletBalance;
        this.busId = busId;
        this.busNumber = busNumber;
        this.busRoute = busRoute;
    }

    public WithdrawalTransactionDto(UUID id, BigDecimal amount, LocalDateTime happenedAt, TransactionStatus status, String note, UUID fromWalletId, String fromWalletNumber, UUID toWalletId, String toWalletNumber, UUID toBankAccountId, String toBankName, String toAccountNumber) {
        this.transactionId = id;
        this.amount = amount;
        this.happenedAt = happenedAt;
        this.status = status;
        this.note = note;
        this.fromWalletId = fromWalletId;
        this.fromWalletNumber = fromWalletNumber;
        this.toWalletId = toWalletId;
        this.toWalletNumber = toWalletNumber;
        this.toBankAccountId = toBankAccountId;
        this.toBankName = toBankName;
        this.toAccountNumber = toAccountNumber;
    }

    // Getters and Setters
    public UUID getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(UUID transactionId) {
        this.transactionId = transactionId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDateTime getHappenedAt() {
        return happenedAt;
    }

    public void setHappenedAt(LocalDateTime happenedAt) {
        this.happenedAt = happenedAt;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getWithdrawalType() {
        return withdrawalType;
    }

    public void setWithdrawalType(String withdrawalType) {
        this.withdrawalType = withdrawalType;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(UUID ownerId) {
        this.ownerId = ownerId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public void setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
    }

    public UUID getFromWalletId() {
        return fromWalletId;
    }

    public void setFromWalletId(UUID fromWalletId) {
        this.fromWalletId = fromWalletId;
    }

    public String getFromWalletNumber() {
        return fromWalletNumber;
    }

    public void setFromWalletNumber(String fromWalletNumber) {
        this.fromWalletNumber = fromWalletNumber;
    }

    public BigDecimal getFromWalletBalance() {
        return fromWalletBalance;
    }

    public void setFromWalletBalance(BigDecimal fromWalletBalance) {
        this.fromWalletBalance = fromWalletBalance;
    }

    public String getFromWalletType() {
        return fromWalletType;
    }

    public void setFromWalletType(String fromWalletType) {
        this.fromWalletType = fromWalletType;
    }

    public UUID getToBankAccountId() {
        return toBankAccountId;
    }

    public void setToBankAccountId(UUID toBankAccountId) {
        this.toBankAccountId = toBankAccountId;
    }

    public String getToBankName() {
        return toBankName;
    }

    public void setToBankName(String toBankName) {
        this.toBankName = toBankName;
    }

    public String getToAccountNumber() {
        return toAccountNumber;
    }

    public void setToAccountNumber(String toAccountNumber) {
        this.toAccountNumber = toAccountNumber;
    }

    public UUID getToWalletId() {
        return toWalletId;
    }

    public void setToWalletId(UUID toWalletId) {
        this.toWalletId = toWalletId;
    }

    public String getToWalletNumber() {
        return toWalletNumber;
    }

    public void setToWalletNumber(String toWalletNumber) {
        this.toWalletNumber = toWalletNumber;
    }

    public BigDecimal getToWalletBalance() {
        return toWalletBalance;
    }

    public void setToWalletBalance(BigDecimal toWalletBalance) {
        this.toWalletBalance = toWalletBalance;
    }

    public UUID getBusId() {
        return busId;
    }

    public void setBusId(UUID busId) {
        this.busId = busId;
    }

    public String getBusNumber() {
        return busNumber;
    }

    public void setBusNumber(String busNumber) {
        this.busNumber = busNumber;
    }

    public String getBusRoute() {
        return busRoute;
    }

    public void setBusRoute(String busRoute) {
        this.busRoute = busRoute;
    }
}