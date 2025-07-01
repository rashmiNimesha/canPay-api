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
@NoArgsConstructor
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

    // Business Constructor
    public User(String name, String nic, String email, UserRole role, UserStatus status) {
        this.name = name;
        this.nic = nic;
        this.email = email;
        this.role = role;
        this.status = status;
    }
}