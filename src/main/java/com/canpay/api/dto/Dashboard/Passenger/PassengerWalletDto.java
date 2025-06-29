package com.canpay.api.dto.Dashboard.Passenger;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.canpay.api.entity.PassengerWallet;

public class PassengerWalletDto {
    private UUID id; // corresponds to entity id
    private String number;
    private BigDecimal balance;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // No-argument constructor
    public PassengerWalletDto() {
        // no-arg constructor for serialization/deserialization
    }

    // Constructor from entity (assuming entity class PassengerWallet)
    public PassengerWalletDto(PassengerWallet entity) {
        if (entity != null) {
            this.id = entity.getId();
            this.number = entity.getWalletNumber();
            this.balance = entity.getBalance();
            this.createdAt = entity.getCreatedAt();
            this.updatedAt = entity.getUpdatedAt();
        }
    }

    // Optional: custom constructor for partial initialization
    public PassengerWalletDto(BigDecimal balance, String passengerId) {
        this.balance = balance;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

}
