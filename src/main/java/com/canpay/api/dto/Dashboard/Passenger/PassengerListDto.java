package com.canpay.api.dto.Dashboard.Passenger;

import java.util.UUID;

import com.canpay.api.entity.User;
import com.canpay.api.entity.User.UserStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PassengerListDto {
    private UUID id;
    private String name;
    private String email;
    private String nic;
    private String photo;
    private UserStatus status;
    private PassengerListWalletDto wallet;

    // No-argument constructor
    public PassengerListDto() {
    }

    // Constructor from User entity
    public PassengerListDto(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.nic = user.getNic();
        this.photo = user.getPhotoUrl();
        this.status = user.getStatus();
    }

    public void setWallet(PassengerListWalletDto walletDto) {
        if (walletDto != null) {
            this.wallet = new PassengerListWalletDto();
            this.wallet.setNumber(walletDto.getNumber());
            this.wallet.setBalance(walletDto.getBalance());
        } else {
            this.wallet = null;
        }
    }
}
