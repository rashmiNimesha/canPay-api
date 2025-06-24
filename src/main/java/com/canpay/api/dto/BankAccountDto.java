package com.canpay.api.dto;

public class BankAccountDto {

    private String bank;
    private long accountNumber;

    public BankAccountDto(String bank, long accountNumber) {
        this.bank = bank;
        this.accountNumber = accountNumber;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public long getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(long accountNumber) {
        this.accountNumber = accountNumber;
    }
}
