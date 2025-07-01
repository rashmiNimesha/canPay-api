package com.canpay.api.dto;


public class UserWalletBalanceDto {

    private String email;
    private double balance;
    private Long accountNumber;
    private String accountName;

    public UserWalletBalanceDto(String email ,double walletBalance) {
        this.email = email;
       this.balance = walletBalance;

    }

    public UserWalletBalanceDto(double balance, Long accountNumber, String accountName) {
        this.balance = balance;
        this.accountNumber = accountNumber;
        this.accountName = accountName;
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

    public Long getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(Long accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }
}
