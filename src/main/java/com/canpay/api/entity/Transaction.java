package com.canpay.api.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @CreatedDate
    @Column(name = "happened_at", nullable = false)
    private LocalDateTime happenedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatus status = TransactionStatus.PENDING;

    @ManyToOne
    @JoinColumn(name = "passenger_id", nullable = false)
    private User passenger;

    @ManyToOne
    @JoinColumn(name = "bus_id")
    private Bus bus;

    @ManyToOne
    @JoinColumn(name = "operator_id")
    private User operator;

    @ManyToOne
    @JoinColumn(name = "from_bank_account_id")
    private BankAccount fromBankAccount;

    @ManyToOne
    @JoinColumn(name = "to_bank_account_id")
    private BankAccount toBankAccount;

    @ManyToOne
    @JoinColumn(name = "from_wallet_id")
    private PassengerWallet fromWallet;

    @ManyToOne
    @JoinColumn(name = "to_wallet_id")
    private BusWallet toWallet;

    @Column(length = 500)
    private String note;

    public enum TransactionType {
        PAYMENT, TOP_UP, WITHDRAWAL, REFUND
    }

    public enum TransactionStatus {
        PENDING, APPROVED, REJECTED, BLOCKED, ACTIVE, INACTIVE
    }

    public Transaction() {}

    public Transaction(BigDecimal amount, TransactionType type, User passenger) {
        this.amount = amount;
        this.type = type;
        this.passenger = passenger;
        this.happenedAt = LocalDateTime.now();
    }

    // Getters and setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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

    public PassengerWallet getFromWallet() {
        return fromWallet;
    }

    public void setFromWallet(PassengerWallet fromWallet) {
        this.fromWallet = fromWallet;
    }

    public BusWallet getToWallet() {
        return toWallet;
    }

    public void setToWallet(BusWallet toWallet) {
        this.toWallet = toWallet;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}