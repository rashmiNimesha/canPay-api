package com.canpay.api.dto;

import com.canpay.api.entity.PassengerWallet;
import com.canpay.api.entity.User;
import com.canpay.api.entity.User.UserRole;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
public class UserWalletData {

    private UUID id;
    private String name;
    private String email;
    private UserRole role;
    private PassengerWallet passengerWallet;


    public UserWalletData(User updatedUser) {
        this.id = updatedUser.getId();
        this.name = updatedUser.getName();
        this.email = updatedUser.getEmail();
        this.role = updatedUser.getRole();
        this.passengerWallet = updatedUser.getPassengerWallet();
    }
}
