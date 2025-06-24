package com.canpay.api.dto;

public class UserWalletBalanceDto {

    private String email;
    private double balance;

    public UserWalletBalanceDto(double walletBalance) {
       this.balance = walletBalance;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
}
