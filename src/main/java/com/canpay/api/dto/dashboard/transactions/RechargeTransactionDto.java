package com.canpay.api.dto.dashboard.transactions;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.canpay.api.entity.Transaction.TransactionStatus;

/**
 * DTO for recharge transaction details.
 * Represents bank to passenger wallet transactions.
 */
public class RechargeTransactionDto {
    private UUID transactionId;
    private BigDecimal amount;
    private LocalDateTime happenedAt;
    private TransactionStatus status;
    private String note;

    // Passenger details
    private UUID passengerId;
    private String passengerName;
    private String passengerEmail;

    // From Bank Account details
    private UUID fromBankAccountId;
    private String fromBankName;
    private String fromAccountNumber;

    // To Wallet details
    private UUID toWalletId;
    private String toWalletNumber;
    private BigDecimal toWalletBalance;

    public RechargeTransactionDto() {
    }

    public RechargeTransactionDto(UUID transactionId, BigDecimal amount, LocalDateTime happenedAt,
            TransactionStatus status, String note, UUID passengerId, String passengerName,
            String passengerEmail, UUID fromBankAccountId, String fromBankName,
            String fromAccountNumber, UUID toWalletId, String toWalletNumber,
            BigDecimal toWalletBalance) {
        this.transactionId = transactionId;
        this.amount = amount;
        this.happenedAt = happenedAt;
        this.status = status;
        this.note = note;
        this.passengerId = passengerId;
        this.passengerName = passengerName;
        this.passengerEmail = passengerEmail;
        this.fromBankAccountId = fromBankAccountId;
        this.fromBankName = fromBankName;
        this.fromAccountNumber = fromAccountNumber;
        this.toWalletId = toWalletId;
        this.toWalletNumber = toWalletNumber;
        this.toWalletBalance = toWalletBalance;
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

    public UUID getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(UUID passengerId) {
        this.passengerId = passengerId;
    }

    public String getPassengerName() {
        return passengerName;
    }

    public void setPassengerName(String passengerName) {
        this.passengerName = passengerName;
    }

    public String getPassengerEmail() {
        return passengerEmail;
    }

    public void setPassengerEmail(String passengerEmail) {
        this.passengerEmail = passengerEmail;
    }

    public UUID getFromBankAccountId() {
        return fromBankAccountId;
    }

    public void setFromBankAccountId(UUID fromBankAccountId) {
        this.fromBankAccountId = fromBankAccountId;
    }

    public String getFromBankName() {
        return fromBankName;
    }

    public void setFromBankName(String fromBankName) {
        this.fromBankName = fromBankName;
    }

    public String getFromAccountNumber() {
        return fromAccountNumber;
    }

    public void setFromAccountNumber(String fromAccountNumber) {
        this.fromAccountNumber = fromAccountNumber;
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
}