package com.canpay.api.dto.Dashboard.Passenger;

import java.util.List;
import java.util.UUID;
import java.time.LocalDateTime;

import com.canpay.api.dto.Dashboard.DBankAccountDto;
import com.canpay.api.entity.User;
import com.canpay.api.entity.User.UserRole;
import com.canpay.api.entity.User.UserStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PassengerDto {
    private UUID id;
    private String name;
    private String email;
    private String nic;
    private String photo;
    private UserRole role;
    private UserStatus status;
    private List<DBankAccountDto> bankAccounts;
    private PassengerWalletDto wallet;
    private List<PassengerTransactionDto> transactions;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // No-argument constructor
    public PassengerDto() {
    }

    // Constructor from User entity
    public PassengerDto(User user) {
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
    public PassengerDto(String name, String email, String nic, UserRole role) {
        this.name = name;
        this.email = email;
        this.nic = nic;
        this.role = role;
    }
}
