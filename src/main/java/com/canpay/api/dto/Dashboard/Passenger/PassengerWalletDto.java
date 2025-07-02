package com.canpay.api.dto.Dashboard.Passenger;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.canpay.api.entity.PassengerWallet;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PassengerWalletDto {
    private UUID id;
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
}
