package com.canpay.api.dto.Dashboard.Passenger;

import java.util.List;

import com.canpay.api.dto.BankAccountDto;

public class PassengerRegistrationRequestDto {
    private String name;
    private String nic;
    private String email;
    private String photo;
    private List<BankAccountDto> bankAccounts;

    // Getters and setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getNic() { return nic; }
    public void setNic(String nic) { this.nic = nic; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getProfilePhotoUrl() { return photo; }
    public void setProfilePhotoUrl(String photo) { this.photo = photo; }
    public List<BankAccountDto> getBankAccounts() { return bankAccounts; }
    public void setBankAccounts(List<BankAccountDto> bankAccounts) { this.bankAccounts = bankAccounts; }
}