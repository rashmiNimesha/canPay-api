package com.canpay.api.dto.dashboard.transactions;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class BusTransactionDto {
    private String transactionId;
    private LocalDateTime happenedAt;
    private String busNumber;
    private String busRoute;
    private String busType;
    private String province;
    private String passengerId;
    private String passengerName;
    private String passengerEmail;
    private String operatorId;
    private String operatorName;
    private String operatorEmail;
    private String note;
    private BigDecimal amount;



    public BusTransactionDto() {
    }

    public BusTransactionDto(String busNumber, String busRoute, String busType, String province) {
        this.busNumber = busNumber;
        this.busRoute = busRoute;
        this.busType = busType;
        this.province = province;
    }

    public BusTransactionDto(String busNumber, String busRoute, String busType, String province, String passengerId, String passengerName, String passengerEmail, String operatorId, String operatorName, String operatorEmail, String note, BigDecimal amount) {
        this.busNumber = busNumber;
        this.busRoute = busRoute;
        this.busType = busType;
        this.province = province;
        this.passengerId = passengerId;
        this.passengerName = passengerName;
        this.passengerEmail = passengerEmail;
        this.operatorId = operatorId;
        this.operatorName = operatorName;
        this.operatorEmail = operatorEmail;
        this.note = note;
        this.amount = amount;
    }

    public BusTransactionDto(String transactionId, LocalDateTime happenedAt, String busNumber, String busRoute, String busType, String province, String passengerId, String passengerName, String passengerEmail, String operatorId, String operatorName, String operatorEmail, String note, BigDecimal amount) {
        this.transactionId = transactionId;
        this.happenedAt = happenedAt;
        this.busNumber = busNumber;
        this.busRoute = busRoute;
        this.busType = busType;
        this.province = province;
        this.passengerId = passengerId;
        this.passengerName = passengerName;
        this.passengerEmail = passengerEmail;
        this.operatorId = operatorId;
        this.operatorName = operatorName;
        this.operatorEmail = operatorEmail;
        this.note = note;
        this.amount = amount;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public LocalDateTime getHappenedAt() {
        return happenedAt;
    }

    public void setHappenedAt(LocalDateTime happenedAt) {
        this.happenedAt = happenedAt;
    }

    public String getBusNumber() {
        return busNumber;
    }

    public void setBusNumber(String busNumber) {
        this.busNumber = busNumber;
    }

    public String getBusRoute() {
        return busRoute;
    }

    public void setBusRoute(String busRoute) {
        this.busRoute = busRoute;
    }

    public String getBusType() {
        return busType;
    }

    public void setBusType(String busType) {
        this.busType = busType;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(String passengerId) {
        this.passengerId = passengerId;
    }

    public String getPassengerName() {
        return passengerName;
    }

    public void setPassengerName(String passengerName) {
        this.passengerName = passengerName;
    }

    public String getPassengerEmail() {
        return passengerEmail;
    }

    public void setPassengerEmail(String passengerEmail) {
        this.passengerEmail = passengerEmail;
    }

    public String getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(String operatorId) {
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

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
