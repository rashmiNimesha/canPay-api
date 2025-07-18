package com.canpay.api.dto.dashboard.user;

import java.util.UUID;

import com.canpay.api.entity.User;
import com.canpay.api.entity.User.UserStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserListDto {
    private UUID id;
    private String name;
    private String email;
    private String nic;
    private String photo;
    private UserStatus status;
    private UserListWalletDto wallet;

    // No-argument constructor
    public UserListDto() {
    }

    // Constructor from User entity
    public UserListDto(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.nic = user.getNic();
        this.photo = user.getPhotoUrl();
        this.status = user.getStatus();
    }

    public void setWallet(UserListWalletDto walletDto) {
        if (walletDto != null) {
            this.wallet = new UserListWalletDto();
            this.wallet.setNumber(walletDto.getNumber());
            this.wallet.setBalance(walletDto.getBalance());
        } else {
            this.wallet = null;
        }
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNic() {
        return nic;
    }

    public void setNic(String nic) {
        this.nic = nic;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public UserListWalletDto getWallet() {
        return wallet;
    }
}
