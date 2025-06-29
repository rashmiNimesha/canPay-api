package com.canpay.api.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Table(name = "operator_assignments")
@Getter
@Setter
@NoArgsConstructor
public class OperatorAssignment extends BaseEntity {

    @CreatedDate
    @Column(name = "assigned_at", nullable = false)
    private LocalDateTime assignedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull
    private AssignmentStatus status = AssignmentStatus.PENDING;

    // Relationships
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    @NotNull
    private User user;

    @ManyToOne
    @JoinColumn(name = "bus_id", nullable = false)
    @JsonBackReference
    @NotNull
    private Bus bus;

    // Enums
    public enum AssignmentStatus {
        PENDING, APPROVED, REJECTED, BLOCKED, ACTIVE, INACTIVE
    }

    // Business Constructor
    public OperatorAssignment(User user, Bus bus, AssignmentStatus status) {
        this.user = user;
        this.bus = bus;
        this.status = status;
        this.assignedAt = LocalDateTime.now();
    }
}