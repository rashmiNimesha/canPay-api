package com.canpay.api.dto.Dashboard.Passenger;

import java.util.List;
import java.util.UUID;
import java.time.LocalDateTime;

import com.canpay.api.dto.BankAccountDto;
import com.canpay.api.entity.User;
import com.canpay.api.entity.User.UserRole;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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
}
