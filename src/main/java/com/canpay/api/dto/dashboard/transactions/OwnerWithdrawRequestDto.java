package com.canpay.api.dto.dashboard.transactions;

import java.math.BigDecimal;

public class OwnerWithdrawRequestDto {

    public enum FromType { BUS, OWNER }
    public enum ToType { WALLET, BANK }

    private FromType fromType; // BUS or OWNER
    private String fromId; // busNumber or ownerId
    private ToType toType; // WALLET or BANK
    private BigDecimal amount;

    public FromType getFromType() { return fromType; }
    public void setFromType(FromType fromType) { this.fromType = fromType; }

    public String getFromId() { return fromId; }
    public void setFromId(String fromId) { this.fromId = fromId; }

    public ToType getToType() { return toType; }
    public void setToType(ToType toType) { this.toType = toType; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
}





