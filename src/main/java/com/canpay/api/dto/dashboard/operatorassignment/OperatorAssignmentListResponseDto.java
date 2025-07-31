package com.canpay.api.dto.dashboard.operatorassignment;

import com.canpay.api.entity.OperatorAssignment.AssignmentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for operator assignment response data.
 */
public class OperatorAssignmentListResponseDto {

    private UUID id;
    private UUID operatorId;
    private String operatorName;
    private UUID busId;
    private String busNumber;
    private UUID busOwnerId;
    private String busOwnerName;
    private AssignmentStatus status;
    private LocalDateTime assignedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    //Rashmi added
    private String operatorEmail;
    private String busRouteFrom;
    private String busRouteTo;
    private BigDecimal busWalletBalance;

    public OperatorAssignmentListResponseDto() {
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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

    public UUID getBusId() {
        return busId;
    }

    public void setBusId(UUID busId) {
        this.busId = busId;
    }

    public String getBusNumber() {
        return busNumber;
    }

    public void setBusNumber(String busNumber) {
        this.busNumber = busNumber;
    }

    public UUID getBusOwnerId() {
        return busOwnerId;
    }

    public void setBusOwnerId(UUID busOwnerId) {
        this.busOwnerId = busOwnerId;
    }

    public String getBusOwnerName() {
        return busOwnerName;
    }

    public void setBusOwnerName(String busOwnerName) {
        this.busOwnerName = busOwnerName;
    }

    public AssignmentStatus getStatus() {
        return status;
    }

    public void setStatus(AssignmentStatus status) {
        this.status = status;
    }

    public LocalDateTime getAssignedAt() {
        return assignedAt;
    }

    public void setAssignedAt(LocalDateTime assignedAt) {
        this.assignedAt = assignedAt;
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

    public String getOperatorEmail() {
        return operatorEmail;
    }

    public void setOperatorEmail(String operatorEmail) {
        this.operatorEmail = operatorEmail;
    }

    public String getBusRouteFrom() {
        return busRouteFrom;
    }

    public void setBusRouteFrom(String busRouteFrom) {
        this.busRouteFrom = busRouteFrom;
    }

    public String getBusRouteTo() {
        return busRouteTo;
    }

    public void setBusRouteTo(String busRouteTo) {
        this.busRouteTo = busRouteTo;
    }

    public BigDecimal getBusWalletBalance() {
        return busWalletBalance;
    }

    public void setBusWalletBalance(BigDecimal busWalletBalance) {
        this.busWalletBalance = busWalletBalance;
    }
}