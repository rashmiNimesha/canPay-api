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

@Entity
@Table(name = "bank_accounts")
@Getter
@Setter
@NoArgsConstructor
public class BankAccount extends BaseEntity {

    @Column(name = "bank_name", nullable = false)
    @NotBlank
    @Size(max = 100)
    private String bankName;

    @Column(name = "account_number", nullable = false)
    @NotNull
    @Positive
    private Long accountNumber;

    @Column(name = "account_name", nullable = false)
    @NotBlank
    @Size(max = 100)
    private String accountName;

    @Column(name = "is_default", nullable = false)
    private boolean isDefault;

    // Relationships
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    @NotNull
    private User user;

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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
