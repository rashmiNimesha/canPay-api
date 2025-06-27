package com.canpay.api.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.util.UUID;

@Entity
public class BankAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String bankName;
    private long accountNumber;
    private String accountHolderName;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;


    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public long getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(long accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getAccountHolderName() {
        return accountHolderName;
    }

    public void setAccountHolderName(String accountHolderName) {
        this.accountHolderName = accountHolderName;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
