package com.canpay.api.dto.dashboard.operatorassignment;

import com.canpay.api.entity.OperatorAssignment.AssignmentStatus;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for operator assignment creation and update requests.
 */
public class OperatorAssignmentRequestDto {

    @NotNull(message = "Operator ID is required")
    private UUID operatorId;

    @NotNull(message = "Bus ID is required")
    private UUID busId;

    private AssignmentStatus status;

    private LocalDateTime assignedAt;

    public OperatorAssignmentRequestDto() {
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
}