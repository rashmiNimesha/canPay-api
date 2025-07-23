package com.canpay.api.dto.dashboard.operatorassignment;

import com.canpay.api.entity.OperatorAssignment.AssignmentStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO for operator assignment search filters.
 */
public class OperatorAssignmentSearchDto {

    private UUID operatorId;
    private UUID busId;
    private UUID busOwnerId;
    private AssignmentStatus status;
    private List<AssignmentStatus> statuses;
    private LocalDateTime assignedAfter;
    private LocalDateTime assignedBefore;
    private LocalDateTime createdAfter;
    private LocalDateTime createdBefore;
    private LocalDateTime updatedAfter;
    private LocalDateTime updatedBefore;

    public OperatorAssignmentSearchDto() {
    }

    // Getters and Setters
    public UUID getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(UUID operatorId) {
        this.operatorId = operatorId;
    }

    public UUID getBusId() {
        return busId;
    }

    public void setBusId(UUID busId) {
        this.busId = busId;
    }

    public UUID getBusOwnerId() {
        return busOwnerId;
    }

    public void setBusOwnerId(UUID busOwnerId) {
        this.busOwnerId = busOwnerId;
    }

    public AssignmentStatus getStatus() {
        return status;
    }

    public void setStatus(AssignmentStatus status) {
        this.status = status;
    }

    public List<AssignmentStatus> getStatuses() {
        return statuses;
    }

    public void setStatuses(List<AssignmentStatus> statuses) {
        this.statuses = statuses;
    }

    public LocalDateTime getAssignedAfter() {
        return assignedAfter;
    }

    public void setAssignedAfter(LocalDateTime assignedAfter) {
        this.assignedAfter = assignedAfter;
    }

    public LocalDateTime getAssignedBefore() {
        return assignedBefore;
    }

    public void setAssignedBefore(LocalDateTime assignedBefore) {
        this.assignedBefore = assignedBefore;
    }

    public LocalDateTime getCreatedAfter() {
        return createdAfter;
    }

    public void setCreatedAfter(LocalDateTime createdAfter) {
        this.createdAfter = createdAfter;
    }

    public LocalDateTime getCreatedBefore() {
        return createdBefore;
    }

    public void setCreatedBefore(LocalDateTime createdBefore) {
        this.createdBefore = createdBefore;
    }

    public LocalDateTime getUpdatedAfter() {
        return updatedAfter;
    }

    public void setUpdatedAfter(LocalDateTime updatedAfter) {
        this.updatedAfter = updatedAfter;
    }

    public LocalDateTime getUpdatedBefore() {
        return updatedBefore;
    }

    public void setUpdatedBefore(LocalDateTime updatedBefore) {
        this.updatedBefore = updatedBefore;
    }
}