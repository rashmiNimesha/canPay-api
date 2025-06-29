package com.canpay.api.dto.Dashboard;

import com.canpay.api.entity.BankAccount;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class DBankAccountDto {
    private String id;
    private String bankName;
    private Long accountNumber;
    private String accountName;
    private boolean isDefault;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Add this no-argument constructor
    public DBankAccountDto() {
        // no-arg constructor for Jackson
    }

    // Constructor from entity
    public DBankAccountDto(BankAccount entity) {
        this.id = entity.getId().toString();
        this.bankName = entity.getBankName();
        this.accountNumber = entity.getAccountNumber();
        this.accountName = entity.getAccountName();
        this.isDefault = entity.isDefault();
        this.createdAt = entity.getCreatedAt();
        this.updatedAt = entity.getUpdatedAt();
    }

    // Optionally keep the old constructor if needed
    public DBankAccountDto(String bankName, Long accountNumber, String accountName) {
        this.bankName = bankName;
        this.accountNumber = accountNumber;
        this.accountName = accountName;
    }
}
