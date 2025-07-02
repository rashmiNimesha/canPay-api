package com.canpay.api.dto.Dashboard.Passenger;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PassengerTransactionDto {
    private UUID id;
    private String transactionNumber;
    private BigDecimal amount;
    private String type; // e.g., "DEBIT", "CREDIT", "TRANSFER"
    private String status; // e.g., "PENDING", "COMPLETED", "FAILED"
    private String description;
    private UUID fromWalletId;
    private UUID toWalletId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // No-argument constructor
    public PassengerTransactionDto() {
    }

    // Constructor with basic fields
    public PassengerTransactionDto(UUID id, String transactionNumber, BigDecimal amount, String type, String status) {
        this.id = id;
        this.transactionNumber = transactionNumber;
        this.amount = amount;
        this.type = type;
        this.status = status;
    }
}
