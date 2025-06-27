package com.canpay.api.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Setter
@Getter
@Entity
public class User {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
        name = "UUID",
        strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(updatable = false, nullable = false)
    private UUID id;

    private String email;

    private String name;

    private String role = "";

    private String nic;

    private String profilePhotoUrl;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<BankAccount> bankAccounts;

    private double walletBalance = 0.0;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<RechargeTransaction> rechargeHistory = new ArrayList<>();


    public User(UUID id, String email, String name, String role, String nic) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.role = role;
        this.nic = nic;
    }

    public User() {

    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getNic() {
        return nic;
    }

    public void setNic(String nic) {
        this.nic = nic;
    }

    public List<BankAccount> getBankAccounts() {
        return bankAccounts;
    }

    public void setBankAccounts(List<BankAccount> bankAccounts) {
        this.bankAccounts = bankAccounts;
    }

    public double getWalletBalance() {
        return walletBalance;
    }

    public void setWalletBalance(double walletBalance) {
        this.walletBalance = walletBalance;
    }

    public List<RechargeTransaction> getRechargeHistory() {
        return rechargeHistory;
    }

    public void setRechargeHistory(List<RechargeTransaction> rechargeHistory) {
        this.rechargeHistory = rechargeHistory;
    }


    public String getProfilePhotoUrl() {
        return profilePhotoUrl;
    }

    public void setProfilePhotoUrl(String profilePhotoUrl) {
        this.profilePhotoUrl = profilePhotoUrl;
    }
}
