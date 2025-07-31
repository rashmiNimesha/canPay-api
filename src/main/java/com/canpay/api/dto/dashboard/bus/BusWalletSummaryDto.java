package com.canpay.api.dto.dashboard.bus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class BusWalletSummaryDto {
    private UUID busid;
    private String busNumber;
    private String walletNumber;
    private UUID walletId;
    private BigDecimal walletBalance;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String routeFrom;
    private String routeTo;
    private String province;
    private String busStatus;
    private UUID operatorId;
    private String operatorName;
    private String operatorEmail;
    private BigDecimal todaysEarnings;

    public BusWalletSummaryDto() {
    }

    public BusWalletSummaryDto(UUID id, String busNumber, String s, BigDecimal bigDecimal, String routeFrom, String routeTo, String province, String name, UUID operatorId, String operatorName, String operatorEmail, BigDecimal todaysEarnings) {
        this.busid = id;
        this.busNumber = busNumber;
        this.walletNumber = s;
        this.walletBalance = bigDecimal;
        this.routeFrom = routeFrom;
        this.routeTo = routeTo;
        this.province = province;
        this.operatorId = operatorId;
        this.operatorName = operatorName;
        this.operatorEmail = operatorEmail;
        this.todaysEarnings = todaysEarnings;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public UUID getBusid() {
        return busid;
    }

    public void setBusid(UUID busid) {
        this.busid = busid;
    }

    public String getBusNumber() {
        return busNumber;
    }

    public void setBusNumber(String busNumber) {
        this.busNumber = busNumber;
    }

    public UUID getWalletId() {
        return walletId;
    }

    public void setWalletId(UUID walletId) {
        this.walletId = walletId;
    }

    public String getWalletNumber() {
        return walletNumber;
    }

    public void setWalletNumber(String walletNumber) {
        this.walletNumber = walletNumber;
    }

    public BigDecimal getWalletBalance() {
        return walletBalance;
    }

    public void setWalletBalance(BigDecimal walletBalance) {
        this.walletBalance = walletBalance;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getRouteFrom() {
        return routeFrom;
    }

    public void setRouteFrom(String routeFrom) {
        this.routeFrom = routeFrom;
    }

    public String getRouteTo() {
        return routeTo;
    }

    public void setRouteTo(String routeTo) {
        this.routeTo = routeTo;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getBusStatus() {
        return busStatus;
    }

    public void setBusStatus(String busStatus) {
        this.busStatus = busStatus;
    }

    public UUID getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(UUID operatorId) {
        this.operatorId = operatorId;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public String getOperatorEmail() {
        return operatorEmail;
    }

    public void setOperatorEmail(String operatorEmail) {
        this.operatorEmail = operatorEmail;
    }

    public BigDecimal getTodaysEarnings() {
        return todaysEarnings;
    }

    public void setTodaysEarnings(BigDecimal todaysEarnings) {
        this.todaysEarnings = todaysEarnings;
    }
}
