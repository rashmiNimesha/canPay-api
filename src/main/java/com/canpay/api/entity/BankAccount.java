package com.canpay.api.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a bank account entity in the system.
 */
@Entity
@Table(name = "bank_accounts")
@Getter
@Setter
@NoArgsConstructor
public class BankAccount extends BaseEntity {
    /** Name of the bank. */
    @Column(name = "bank_name", nullable = false)
    @NotBlank
    @Size(max = 100)
    private String bankName;

    /** Account number of the bank account. */
    @Column(name = "account_number", nullable = false)
    @NotNull
    @Positive
    private Long accountNumber;

    /** Name of the account holder. */
    @Column(name = "account_name", nullable = false)
    @NotBlank
    @Size(max = 100)
    private String accountName;

    /** Indicates if this is the default bank account. */
    @Column(name = "is_default", nullable = false)
    private boolean isDefault;

    /** The user who owns this bank account. */
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    @NotNull
    private User user;

    // Getters and Setters
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

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
