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

    // Business Constructor
    public BankAccount(User user, String bankName, Long accountNumber, String accountName, boolean isDefault) {
        this.user = user;
        this.bankName = bankName;
        this.accountNumber = accountNumber;
        this.accountName = accountName;
        this.isDefault = isDefault;
    }
}