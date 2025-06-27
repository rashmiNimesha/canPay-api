package com.canpay.api.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class RechargeTransaction {

    @Id
    @GeneratedValue
    private UUID id;
    private double amount;

    @CreatedDate
    private LocalDateTime timestamp;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public RechargeTransaction(UUID id, double amount, LocalDateTime timestamp, User user) {
        this.id = id;
        this.amount = amount;
        this.timestamp = timestamp;
        this.user = user;
    }

    public RechargeTransaction() {

    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
