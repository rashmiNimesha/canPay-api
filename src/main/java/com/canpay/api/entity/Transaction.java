package com.canpay.api.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Represents a transaction entity in the system.
 */
@Entity
@Table(name = "transactions")
@Getter
@Setter
public class Transaction extends BaseEntity {
    /** Transaction amount. */
    @Column(nullable = false, precision = 19, scale = 2)
    @NotNull
    @DecimalMin(value = "0.01")
    private BigDecimal amount;

    /** Timestamp when the transaction happened. */
    @CreatedDate
    @Column(name = "happened_at", nullable = false)
    private LocalDateTime happenedAt;

    /** Type of the transaction. */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull
    private TransactionType type;

    /** Status of the transaction. */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull
    private TransactionStatus status = TransactionStatus.PENDING;

    /** Optional note for the transaction. */
    @Column(length = 500)
    @Size(max = 500)
    private String note;

    /** Passenger involved in the transaction. */
    @ManyToOne
    @JoinColumn(name = "passenger_id")
    private User passenger;

    /** Bus involved in the transaction, if any. */
    @ManyToOne
    @JoinColumn(name = "bus_id")
    private Bus bus;

    /** Operator involved in the transaction, if any. */
    @ManyToOne
    @JoinColumn(name = "operator_id")
    private User operator;

    /** Owner involved in the transaction, if any. */
    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    /** Source bank account for the transaction, if any. */
    @ManyToOne
    @JoinColumn(name = "from_bank_account_id")
    private BankAccount fromBankAccount;

    /** Destination bank account for the transaction, if any. */
    @ManyToOne
    @JoinColumn(name = "to_bank_account_id")
    private BankAccount toBankAccount;

    /** Source wallet for the transaction, if any. */
    @ManyToOne
    @JoinColumn(name = "from_wallet_id")
    private Wallet fromWallet;

    /** Destination wallet for the transaction, if any. */
    @ManyToOne
    @JoinColumn(name = "to_wallet_id")
    private Wallet toWallet;

    // Business Constructor
    public Transaction(BigDecimal amount, TransactionType type, User passenger) {
        this.amount = amount;
        this.type = type;
        this.passenger = passenger;
        this.happenedAt = LocalDateTime.now();
    }

    public Transaction(BigDecimal amount, TransactionType type, User passenger, Bus bus, User operator) {
        this.amount = amount;
        this.type = type;
        this.passenger = passenger;
        this.bus = bus;
        this.operator = operator;
        this.happenedAt = LocalDateTime.now();
    }

    public Transaction() {
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDateTime getHappenedAt() {
        return happenedAt;
    }

    public void setHappenedAt(LocalDateTime happenedAt) {
        this.happenedAt = happenedAt;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public User getPassenger() {
        return passenger;
    }

    public void setPassenger(User passenger) {
        this.passenger = passenger;
    }

    public Bus getBus() {
        return bus;
    }

    public void setBus(Bus bus) {
        this.bus = bus;
    }

    public User getOperator() {
        return operator;
    }

    public void setOperator(User operator) {
        this.operator = operator;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public BankAccount getFromBankAccount() {
        return fromBankAccount;
    }

    public void setFromBankAccount(BankAccount fromBankAccount) {
        this.fromBankAccount = fromBankAccount;
    }

    public BankAccount getToBankAccount() {
        return toBankAccount;
    }

    public void setToBankAccount(BankAccount toBankAccount) {
        this.toBankAccount = toBankAccount;
    }

    public Wallet getFromWallet() {
        return fromWallet;
    }

    public void setFromWallet(Wallet fromWallet) {
        this.fromWallet = fromWallet;
    }

    public Wallet getToWallet() {
        return toWallet;
    }

    public void setToWallet(Wallet toWallet) {
        this.toWallet = toWallet;
    }

    // Enums
    public enum TransactionType {
        PAYMENT, RECHARGE, WITHDRAWAL, REFUND
    }

    public enum TransactionStatus {
        PENDING, APPROVED, REJECTED, BLOCKED, ACTIVE, INACTIVE
    }

}