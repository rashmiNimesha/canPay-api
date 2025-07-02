package com.canpay.api.dto.Dashboard.Passenger;

import java.util.List;

import com.canpay.api.dto.Dashboard.DBankAccountDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PassengerRegistrationRequestDto {
    private String name;
    private String nic;
    private String email;
    private String photo;
    private List<DBankAccountDto> bankAccounts;

    // No-argument constructor
    public PassengerRegistrationRequestDto() {
    }

    // Constructor with basic fields
    public PassengerRegistrationRequestDto(String name, String nic, String email, String photo) {
        this.name = name;
        this.nic = nic;
        this.email = email;
        this.photo = photo;
    }
}
