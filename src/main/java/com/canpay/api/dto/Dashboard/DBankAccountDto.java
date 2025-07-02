package com.canpay.api.dto.Dashboard;

import java.util.UUID;
import com.canpay.api.entity.BankAccount;
import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Getter
@Setter
public class DBankAccountDto {
    private UUID id;

    @NotBlank(message = "Bank name is required")
    private String bankName;

    @NotBlank(message = "Account number is required")
    private String accountNumber;

    @NotBlank(message = "Account name is required")
    private String accountName;

    @NotNull(message = "Default status is required")
    private boolean isDefault;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // No-argument constructor
    public DBankAccountDto() {
    }

    // Constructor from entity
    public DBankAccountDto(BankAccount bankAccount) {
        if (bankAccount != null) {
            this.id = bankAccount.getId();
            this.bankName = bankAccount.getBankName();
            this.accountNumber = bankAccount.getAccountNumber() != null ? bankAccount.getAccountNumber().toString()
                    : null;
            this.accountName = bankAccount.getAccountName();
            this.isDefault = bankAccount.isDefault();
            this.createdAt = bankAccount.getCreatedAt();
            this.updatedAt = bankAccount.getUpdatedAt();
        }
    }

    // Constructor for request mapping
    public DBankAccountDto(String bankName, String accountNumber, String accountName, boolean isDefault) {
        this.bankName = bankName;
        this.accountNumber = accountNumber;
        this.accountName = accountName;
        this.isDefault = isDefault;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public @NotBlank(message = "Bank name is required") String getBankName() {
        return bankName;
    }

    public void setBankName(@NotBlank(message = "Bank name is required") String bankName) {
        this.bankName = bankName;
    }

    public @NotBlank(message = "Account number is required") String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(@NotBlank(message = "Account number is required") String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public @NotBlank(message = "Account name is required") String getAccountName() {
        return accountName;
    }

    public void setAccountName(@NotBlank(message = "Account name is required") String accountName) {
        this.accountName = accountName;
    }

    @NotNull(message = "Default status is required")
    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(@NotNull(message = "Default status is required") boolean aDefault) {
        isDefault = aDefault;
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
