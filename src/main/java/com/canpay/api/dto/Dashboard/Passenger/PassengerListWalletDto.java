package com.canpay.api.dto.Dashboard.Passenger;

import java.math.BigDecimal;
import com.canpay.api.entity.PassengerWallet;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PassengerListWalletDto {
    private String number;
    private BigDecimal balance;

    // No-argument constructor
    public PassengerListWalletDto() {
        // no-arg constructor for serialization/deserialization
    }

    // Constructor from entity (assuming entity class PassengerWallet)
    public PassengerListWalletDto(PassengerWallet entity) {
        if (entity != null) {
            this.number = entity.getWalletNumber();
            this.balance = entity.getBalance();
        }
    }
}
