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
@Table(name = "bus_wallets")
@Getter
@Setter
@NoArgsConstructor
public class BusWallet extends BaseEntity {

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
    @JoinColumn(name = "bus_id", nullable = false, unique = true)
    @JsonBackReference
    @NotNull
    private Bus bus;

    // Business Constructor
    public BusWallet(Bus bus, String walletNumber) {
        this.bus = bus;
        this.walletNumber = walletNumber;
        this.balance = BigDecimal.ZERO;
    }
}