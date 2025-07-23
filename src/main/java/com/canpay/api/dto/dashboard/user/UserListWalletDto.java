package com.canpay.api.dto.dashboard.user;

import java.math.BigDecimal;
import com.canpay.api.entity.Wallet;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserListWalletDto {
    private String number;
    private BigDecimal balance;

    // No-argument constructor
    public UserListWalletDto() {
        // no-arg constructor for serialization/deserialization
    }

    // Constructor from entity (using consolidated Wallet entity)
    public UserListWalletDto(Wallet entity) {
        if (entity != null) {
            this.number = entity.getWalletNumber();
            this.balance = entity.getBalance();
        }
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
