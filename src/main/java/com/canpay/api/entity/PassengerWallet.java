package com.canpay.api.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "passenger_wallets")
@Getter
@Setter
@NoArgsConstructor
public class PassengerWallet extends BaseEntity {

    @Column(nullable = false, precision = 19, scale = 2)
    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(name = "wallet_number", nullable = false, unique = true, length = 16)
    @NotBlank
    @Size(min = 16, max = 16)
    private String walletNumber;

    // Relationships
    @OneToOne
    @JoinColumn(name = "passenger_id", nullable = false, unique = true)
    @JsonBackReference
    @NotNull
    private User passenger;

    // Business Constructor
    public PassengerWallet(User passenger) {
        this.passenger = passenger;
        this.balance = BigDecimal.ZERO;
    }

    public PassengerWallet(User passenger, String walletNumber) {
        this.passenger = passenger;
        this.walletNumber = walletNumber;
        this.balance = BigDecimal.ZERO;
    }

    public User getPassenger() {
        return passenger;
    }

    public void setPassenger(User passenger) {
        this.passenger = passenger;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getWalletNumber() {
        return walletNumber;
    }

    public void setWalletNumber(String walletNumber) {
        this.walletNumber = walletNumber;
    }

}