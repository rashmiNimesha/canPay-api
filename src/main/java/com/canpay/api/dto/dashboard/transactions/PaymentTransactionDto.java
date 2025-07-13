package com.canpay.api.dto.dashboard.transactions;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.canpay.api.entity.Transaction.TransactionStatus;

/**
 * DTO for payment transaction details.
 * Represents passenger wallet to bus wallet transactions with full details.
 */
public class PaymentTransactionDto {
    private UUID transactionId;
    private BigDecimal amount;
    private LocalDateTime happenedAt;
    private TransactionStatus status;
    private String note;

    // Passenger details
    private UUID passengerId;
    private String passengerName;
    private String passengerEmail;

    // Operator details
    private UUID operatorId;
    private String operatorName;
    private String operatorEmail;

    // Owner details
    private UUID ownerId;
    private String ownerName;
    private String ownerEmail;

    // Bus details
    private UUID busId;
    private String busNumber;
    private String busRoute;
    private String busType;
    private String province;

    // From Wallet details (passenger wallet)
    private UUID fromWalletId;
    private String fromWalletNumber;
    private BigDecimal fromWalletBalance;

    // To Wallet details (bus wallet)
    private UUID toWalletId;
    private String toWalletNumber;
    private BigDecimal toWalletBalance;

    public PaymentTransactionDto() {
    }

    public PaymentTransactionDto(UUID transactionId, BigDecimal amount, LocalDateTime happenedAt,
            TransactionStatus status, String note, UUID passengerId, String passengerName,
            String passengerEmail, UUID operatorId, String operatorName, String operatorEmail,
            UUID ownerId, String ownerName, String ownerEmail, UUID busId, String busNumber,
            String busRoute, String busType, String province, UUID fromWalletId,
            String fromWalletNumber, BigDecimal fromWalletBalance, UUID toWalletId,
            String toWalletNumber, BigDecimal toWalletBalance) {
        this.transactionId = transactionId;
        this.amount = amount;
        this.happenedAt = happenedAt;
        this.status = status;
        this.note = note;
        this.passengerId = passengerId;
        this.passengerName = passengerName;
        this.passengerEmail = passengerEmail;
        this.operatorId = operatorId;
        this.operatorName = operatorName;
        this.operatorEmail = operatorEmail;
        this.ownerId = ownerId;
        this.ownerName = ownerName;
        this.ownerEmail = ownerEmail;
        this.busId = busId;
        this.busNumber = busNumber;
        this.busRoute = busRoute;
        this.busType = busType;
        this.province = province;
        this.fromWalletId = fromWalletId;
        this.fromWalletNumber = fromWalletNumber;
        this.fromWalletBalance = fromWalletBalance;
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

    public UUID getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(UUID operatorId) {
        this.operatorId = operatorId;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public String getOperatorEmail() {
        return operatorEmail;
    }

    public void setOperatorEmail(String operatorEmail) {
        this.operatorEmail = operatorEmail;
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

    public String getBusType() {
        return busType;
    }

    public void setBusType(String busType) {
        this.busType = busType;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
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