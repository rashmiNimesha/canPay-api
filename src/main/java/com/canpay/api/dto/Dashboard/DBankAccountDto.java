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
}
