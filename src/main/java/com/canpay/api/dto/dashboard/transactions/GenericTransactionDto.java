package com.canpay.api.dto.dashboard.transactions;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class GenericTransactionDto {
    private UUID id;
    private String type;
    private String status;
    private BigDecimal amount;
    private LocalDateTime happenedAt;
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

    // Wallet details
    private UUID fromWalletId;
    private String fromWalletNumber;
    private UUID toWalletId;
    private String toWalletNumber;

    // Bank account details
    private String fromBankName;
    private String toBankName;

    public GenericTransactionDto(UUID id, String type, String status, BigDecimal amount, LocalDateTime happenedAt,
            String note, UUID passengerId, String passengerName, String passengerEmail, UUID operatorId,
            String operatorName, String operatorEmail, UUID ownerId, String ownerName, String ownerEmail,
            UUID busId, String busNumber, String busRoute, UUID fromWalletId, String fromWalletNumber,
            UUID toWalletId, String toWalletNumber, String fromBankName, String toBankName) {
        this.id = id;
        this.type = type;
        this.status = status;
        this.amount = amount;
        this.happenedAt = happenedAt;
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
        this.fromWalletId = fromWalletId;
        this.fromWalletNumber = fromWalletNumber;
        this.toWalletId = toWalletId;
        this.toWalletNumber = toWalletNumber;
        this.fromBankName = fromBankName;
        this.toBankName = toBankName;
    }

    // Getters
    public UUID getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getStatus() {
        return status;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public LocalDateTime getHappenedAt() {
        return happenedAt;
    }

    public String getNote() {
        return note;
    }

    public UUID getPassengerId() {
        return passengerId;
    }

    public String getPassengerName() {
        return passengerName;
    }

    public String getPassengerEmail() {
        return passengerEmail;
    }

    public UUID getOperatorId() {
        return operatorId;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public String getOperatorEmail() {
        return operatorEmail;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public UUID getBusId() {
        return busId;
    }

    public String getBusNumber() {
        return busNumber;
    }

    public String getBusRoute() {
        return busRoute;
    }

    public UUID getFromWalletId() {
        return fromWalletId;
    }

    public String getFromWalletNumber() {
        return fromWalletNumber;
    }

    public UUID getToWalletId() {
        return toWalletId;
    }

    public String getToWalletNumber() {
        return toWalletNumber;
    }

    public String getFromBankName() {
        return fromBankName;
    }

    public String getToBankName() {
        return toBankName;
    }
}
