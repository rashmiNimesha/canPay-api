package com.canpay.api.dto;

public class BankAccountDto {

    private String bank;
    private long accountNumber;
    private String accountName;

    public BankAccountDto(String bank, long accountNumber, String accountName) {
        this.bank = bank;
        this.accountNumber = accountNumber;
        this.accountName = accountName;
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

    public String getAccountHolderName() {
        return accountName;
    }

    public void setAccountHolderName(String accountName) {
        this.accountName = accountName;
    }
}
