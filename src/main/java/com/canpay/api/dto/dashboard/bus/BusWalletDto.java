package com.canpay.api.dto.dashboard.bus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.canpay.api.entity.Wallet;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BusWalletDto {
    private UUID id;
    private String number;
    private BigDecimal balance;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // No-argument constructor
    public BusWalletDto() {
        // no-arg constructor for serialization/deserialization
    }

    // Constructor from entity (using consolidated Wallet entity)
    public BusWalletDto(Wallet entity) {
        if (entity != null) {
            this.id = entity.getId();
            this.number = entity.getWalletNumber();
            this.balance = entity.getBalance();
            this.createdAt = entity.getCreatedAt();
            this.updatedAt = entity.getUpdatedAt();
        }
    }

    public BusWalletDto(UUID id, String busNumber, String s, BigDecimal bigDecimal, String routeFrom, String routeTo, String province, String name, UUID operatorId, String operatorName, String operatorEmail, BigDecimal todaysEarnings) {
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