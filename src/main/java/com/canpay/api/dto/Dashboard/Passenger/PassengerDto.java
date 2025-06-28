package com.canpay.api.dto.Dashboard.Passenger;

import java.util.List;
import java.util.UUID;
import java.time.LocalDateTime;

import com.canpay.api.dto.BankAccountDto;
import com.canpay.api.entity.User;
import com.canpay.api.entity.User.UserRole;

public class PassengerDto {
    private UUID id;
    private String name;
    private String email;
    private String nic;
    private UserRole role;
    private List<BankAccountDto> bankAccounts; // Added field
    private PassengerWalletDto wallet; // Added field
    private List<PassengerTransactionDto> transactions; // Added field
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public PassengerDto(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.nic = user.getNic();
        this.role = user.getRole();
        this.createdAt = user.getCreatedAt();
        this.updatedAt = user.getUpdatedAt();
    }

    public PassengerDto(String name, String email, String nic, UserRole role) {
        this.name = name;
        this.email = email;
        this.nic = nic;
        this.role = role;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNic() {
        return nic;
    }

    public void setNic(String nic) {
        this.nic = nic;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public List<BankAccountDto> getBankAccounts() {
        return bankAccounts;
    }

    public void setBankAccounts(List<BankAccountDto> bankAccounts) {
        this.bankAccounts = bankAccounts;
    }

    public PassengerWalletDto getWallet() {
        return wallet;
    }

    public void setWallet(PassengerWalletDto wallet) {
        this.wallet = wallet;
    }

    public List<PassengerTransactionDto> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<PassengerTransactionDto> transactions) {
        this.transactions = transactions;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
