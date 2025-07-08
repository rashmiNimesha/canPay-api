package com.canpay.api.dto.dashboard.user;

import java.util.List;
import java.util.UUID;
import java.time.LocalDateTime;

import com.canpay.api.dto.dashboard.DBankAccountDto;
import com.canpay.api.entity.User;
import com.canpay.api.entity.User.UserRole;
import com.canpay.api.entity.User.UserStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {
    private UUID id;
    private String name;
    private String email;
    private String nic;
    private String photo;
    private UserRole role;
    private UserStatus status;
    private List<DBankAccountDto> bankAccounts;
    private UserWalletDto wallet;
    private List<UserTransactionDto> transactions;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // No-argument constructor
    public UserDto() {
    }

    // Constructor from User entity
    public UserDto(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.nic = user.getNic();
        this.photo = user.getPhotoUrl();
        this.role = user.getRole();
        this.status = user.getStatus();
        this.createdAt = user.getCreatedAt();
        this.updatedAt = user.getUpdatedAt();
    }

    // Constructor for partial initialization
    public UserDto(String name, String email, String nic, UserRole role) {
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

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public List<DBankAccountDto> getBankAccounts() {
        return bankAccounts;
    }

    public void setBankAccounts(List<DBankAccountDto> bankAccounts) {
        this.bankAccounts = bankAccounts;
    }

    public UserWalletDto getWallet() {
        return wallet;
    }

    public void setWallet(UserWalletDto wallet) {
        this.wallet = wallet;
    }

    public List<UserTransactionDto> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<UserTransactionDto> transactions) {
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
