package com.canpay.api.dto;

import com.canpay.api.entity.BankAccount;
import java.time.LocalDateTime;

public class BankAccountDto {

    private String id;
    private String bankName;
    private Long accountNumber;
    private String accountName;
    private boolean isDefault;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Add this no-argument constructor
    public BankAccountDto() {
        // no-arg constructor for Jackson
    }

    // Constructor from entity
    public BankAccountDto(BankAccount entity) {
        this.id = entity.getId().toString();
        this.bankName = entity.getBankName();
        this.accountNumber = entity.getAccountNumber();
        this.accountName = entity.getAccountName();
        this.isDefault = entity.isDefault();
        this.createdAt = entity.getCreatedAt();
        this.updatedAt = entity.getUpdatedAt();
    }

    // Optionally keep the old constructor if needed
    public BankAccountDto(String bankName, Long accountNumber, String accountName) {
        this.bankName = bankName;
        this.accountNumber = accountNumber;
        this.accountName = accountName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public Long getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(Long accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
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
