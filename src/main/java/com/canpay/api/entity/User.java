package com.canpay.api.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User extends BaseEntity {
    @Column(nullable = true)
    @Size(max = 100)
    private String name;

    @Column(nullable = true)
    @Size(max = 20)
    private String nic;

    @Column(nullable = false)
    @NotBlank
    @Email
    @Size(max = 255)
    private String email;

    @Column(name = "photo_url")
    @Size(max = 500)
    private String photoUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull
    private UserRole role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull
    private UserStatus status = UserStatus.ACTIVE;

    // Relationships
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<BankAccount> bankAccounts;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Bus> ownedBuses;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<OperatorAssignment> operatorAssignments;

    @OneToOne(mappedBy = "passenger", cascade = CascadeType.ALL)
    @JsonManagedReference
    private PassengerWallet passengerWallet;

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

    public PassengerWallet getPassengerWallet() {
        return passengerWallet;
    }

    public void setPassengerWallet(PassengerWallet passengerWallet) {
        this.passengerWallet = passengerWallet;
    }
}