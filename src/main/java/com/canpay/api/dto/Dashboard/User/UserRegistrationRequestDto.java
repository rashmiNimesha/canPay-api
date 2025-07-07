package com.canpay.api.dto.Dashboard.User;

import java.util.List;

import com.canpay.api.dto.Dashboard.DBankAccountDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRegistrationRequestDto {
    private String name;
    private String nic;
    private String email;
    private String photo;
    private List<DBankAccountDto> bankAccounts;

    // No-argument constructor
    public UserRegistrationRequestDto() {
    }

    // Constructor with basic fields
    public UserRegistrationRequestDto(String name, String nic, String email, String photo) {
        this.name = name;
        this.nic = nic;
        this.email = email;
        this.photo = photo;
    }

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

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public List<DBankAccountDto> getBankAccounts() {
        return bankAccounts;
    }

    public void setBankAccounts(List<DBankAccountDto> bankAccounts) {
        this.bankAccounts = bankAccounts;
    }
}
