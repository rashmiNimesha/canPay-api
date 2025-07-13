package com.canpay.api.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Represents a user entity in the system.
 */
@Entity
@Table(name = "users")
@Getter
@Setter
public class User extends BaseEntity {
    /** Name of the user. */
    @Column(nullable = true)
    @Size(max = 100)
    private String name;

    /** National identity card number. */
    @Column(nullable = true)
    @Size(max = 20)
    private String nic;

    /** Email address of the user. */
    @Column(nullable = false)
    @NotBlank
    @Email
    @Size(max = 255)
    private String email;

    /** URL to the user's photo. */
    @Column(name = "photo_url")
    @Size(max = 500)
    private String photoUrl;

    /** Role of the user (OWNER, OPERATOR, PASSENGER). */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull
    private UserRole role;

    /** Status of the user. */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull
    private UserStatus status = UserStatus.ACTIVE;

    /** List of bank accounts owned by the user. */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<BankAccount> bankAccounts;

    /** List of buses owned by the user. */
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Bus> ownedBuses;

    /** List of operator assignments for the operator. */
    @OneToMany(mappedBy = "operator", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<OperatorAssignment> operatorAssignments;

    /** Wallet associated with the user. */
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private Wallet wallet;

    // Enums
    public enum UserRole {
        OWNER, OPERATOR, PASSENGER
    }

    public enum UserStatus {
        PENDING, ACTIVE, INACTIVE, BLOCKED
    }

    public User() {
    }

    public User(String name, String nic, String email, UserRole role) {
        this.name = name;
        this.nic = nic;
        this.email = email;
        this.role = role;
    }

    // Business Constructor
    public User(String name, String nic, String email, UserRole role, UserStatus status) {
        this.name = name;
        this.nic = nic;
        this.email = email;
        this.role = role;
        this.status = status;
    }

    public @Size(max = 100) String getName() {
        return name;
    }

    public void setName(@Size(max = 100) String name) {
        this.name = name;
    }

    public @Size(max = 20) String getNic() {
        return nic;
    }

    public void setNic(@Size(max = 20) String nic) {
        this.nic = nic;
    }

    public @NotBlank @Email @Size(max = 255) String getEmail() {
        return email;
    }

    public void setEmail(@NotBlank @Email @Size(max = 255) String email) {
        this.email = email;
    }

    public @Size(max = 500) String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(@Size(max = 500) String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public @NotNull UserRole getRole() {
        return role;
    }

    public void setRole(@NotNull UserRole role) {
        this.role = role;
    }

    public @NotNull UserStatus getStatus() {
        return status;
    }

    public void setStatus(@NotNull UserStatus status) {
        this.status = status;
    }

    public List<BankAccount> getBankAccounts() {
        return bankAccounts;
    }

    public void setBankAccounts(List<BankAccount> bankAccounts) {
        this.bankAccounts = bankAccounts;
    }

    public List<Bus> getOwnedBuses() {
        return ownedBuses;
    }

    public void setOwnedBuses(List<Bus> ownedBuses) {
        this.ownedBuses = ownedBuses;
    }

    public List<OperatorAssignment> getOperatorAssignments() {
        return operatorAssignments;
    }

    public void setOperatorAssignments(List<OperatorAssignment> operatorAssignments) {
        this.operatorAssignments = operatorAssignments;
    }

    public Wallet getWallet() {
        return wallet;
    }

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }

}