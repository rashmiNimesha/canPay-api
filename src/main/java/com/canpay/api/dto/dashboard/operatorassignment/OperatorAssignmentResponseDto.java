package com.canpay.api.dto.dashboard.operatorassignment;

import com.canpay.api.dto.dashboard.bus.BusResponseDto;
import com.canpay.api.dto.dashboard.user.UserDto;
import com.canpay.api.entity.OperatorAssignment.AssignmentStatus;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for operator assignment response data.
 */
public class OperatorAssignmentResponseDto {

    private UUID id;
    private UserDto operator;
    private BusResponseDto bus;
    private AssignmentStatus status;
    private LocalDateTime assignedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean assigned; // Rashmi added

    public OperatorAssignmentResponseDto() {
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UserDto getOperator() {
        return operator;
    }

    public void setOperator(UserDto operator) {
        this.operator = operator;
    }

    public BusResponseDto getBus() {
        return bus;
    }

    public void setBus(BusResponseDto bus) {
        this.bus = bus;
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

    public void setBusId(UUID busid) {
        if (this.bus == null) {
            this.bus = new BusResponseDto();
        }
        this.bus.setId(busid);
    }

    public void setOperatorId(UUID operatorid) {
        if (this.operator == null) {
            this.operator = new UserDto();
        }
        this.operator.setId(operatorid);
    }

    public boolean isAssigned() {
        return assigned;
    }

    public void setAssigned(boolean assigned) {
        this.assigned = assigned;
    }
}