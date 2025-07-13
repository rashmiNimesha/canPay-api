package com.canpay.api.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

/**
 * Represents an operator assignment to a bus.
 */
@Entity
@Table(name = "operator_assignments")
@Getter
@Setter
@NoArgsConstructor
public class OperatorAssignment extends BaseEntity {

    /** Timestamp when the operator was assigned. */
    @CreatedDate
    @Column(name = "assigned_at", nullable = false)
    private LocalDateTime assignedAt;

    /** Status of the assignment. */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull
    private AssignmentStatus status = AssignmentStatus.PENDING;

    /** The user assigned as operator. */
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    @NotNull
    private User operator;

    /** The bus to which the operator is assigned. */
    @ManyToOne
    @JoinColumn(name = "bus_id", nullable = false)
    @JsonBackReference
    @NotNull
    private Bus bus;

    // Enums
    public enum AssignmentStatus {
        PENDING, ACTIVE, INACTIVE, REJECTED, BLOCKED
    }

    // Business Constructor
    public OperatorAssignment(User user, Bus bus, AssignmentStatus status) {
        this.operator = user;
        this.bus = bus;
        this.status = status;
        this.assignedAt = LocalDateTime.now();
    }

    // Explicit Getters and Setters
    public LocalDateTime getAssignedAt() {
        return assignedAt;
    }

    public void setAssignedAt(LocalDateTime assignedAt) {
        this.assignedAt = assignedAt;
    }

    public AssignmentStatus getStatus() {
        return status;
    }

    public void setStatus(AssignmentStatus status) {
        this.status = status;
    }

    public User getUser() {
        return operator;
    }

    public void setUser(User user) {
        this.operator = user;
    }

    public Bus getBus() {
        return bus;
    }

    public void setBus(Bus bus) {
        this.bus = bus;
    }

}