package com.canpay.api.dto.Dashboard.Passenger;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

import com.canpay.api.dto.Dashboard.BankAccountDto;

public class PassengerRegistrationRequestDto {
    // Passenger's full name
    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String name;

    // National Identity Card number
    @NotBlank(message = "NIC is required")
    @Size(max = 12, message = "NIC must not exceed 12 characters")
    private String nic;

    // Email address
    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;

    // Profile photo URL
    private String photo;

    // List of passenger's bank accounts
    private List<BankAccountDto> bankAccounts;

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNic() {
        return nic;
    }

    public void setNic(String nic) {
        this.nic = nic;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfilePhotoUrl() {
        return photo;
    }

    public void setProfilePhotoUrl(String photo) {
        this.photo = photo;
    }

    public List<BankAccountDto> getBankAccounts() {
        return bankAccounts;
    }

    public void setBankAccounts(List<BankAccountDto> bankAccounts) {
        this.bankAccounts = bankAccounts;
    }
}