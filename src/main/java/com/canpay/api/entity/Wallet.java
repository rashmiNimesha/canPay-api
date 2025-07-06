package com.canpay.api.entity;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a wallet entity in the system.
 */
@Entity
@Table(name = "wallets")
@Getter
@Setter
@NoArgsConstructor
public class Wallet extends BaseEntity {

    /** Current balance of the wallet. */
    @Column(nullable = false, precision = 19, scale = 2)
    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal balance = BigDecimal.ZERO;

    /** Unique wallet number. */
    @Column(name = "wallet_number", nullable = false, unique = true, length = 16)
    @NotBlank
    @Size(min = 16, max = 16)
    private String walletNumber;

    /** Type of the wallet (OWNER, PASSENGER, BUS). */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull
    private WalletType type;

    /** The user who owns this wallet, if any. */
    @OneToOne
    @JoinColumn(name = "user_id", nullable = true)
    @JsonBackReference
    private User user;

    /** The bus associated with this wallet, if any. */
    @OneToOne
    @JoinColumn(name = "bus_id", nullable = true)
    @JsonBackReference
    private Bus bus;

    // Enums
    public enum WalletType {
        OWNER, PASSENGER, BUS
    }

    // Business Constructors
    public Wallet(User user, WalletType type) {
        this.user = user;
        this.type = type;
    }

    public Wallet(User user, String walletNumber, WalletType type) {
        this.user = user;
        this.walletNumber = walletNumber;
        this.type = type;
    }

    public Wallet(Bus bus, String walletNumber) {
        this.bus = bus;
        this.walletNumber = walletNumber;
        this.type = WalletType.BUS;
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

    public WalletType getType() {
        return type;
    }

    public void setType(WalletType type) {
        this.type = type;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Bus getBus() {
        return bus;
    }

    public void setBus(Bus bus) {
        this.bus = bus;
    }
}