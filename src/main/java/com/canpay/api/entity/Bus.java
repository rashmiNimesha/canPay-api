package com.canpay.api.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "buses")
public class Bus {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    @JsonBackReference
    private User owner;

    @Column(name = "bus_number", nullable = false)
    private String busNumber;

    @Enumerated(EnumType.STRING)
    private BusType type;

    @Column(name = "route_from")
    private String routeFrom;

    @Column(name = "route_to")
    private String routeTo;

    private String province;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BusStatus status = BusStatus.PENDING;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "bus", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<OperatorAssignment> operatorAssignments;

    @OneToOne(mappedBy = "bus", cascade = CascadeType.ALL)
    @JsonManagedReference
    private BusWallet busWallet;

    public enum BusType {
        NORMAL, SEMI_LUXURY, LUXURY, AC
    }

    public enum BusStatus {
        PENDING, APPROVED, REJECTED, BLOCKED, ACTIVE, INACTIVE
    }

    public Bus() {}

    public Bus(User owner, String busNumber, BusType type, String routeFrom, String routeTo, String province) {
        this.owner = owner;
        this.busNumber = busNumber;
        this.type = type;
        this.routeFrom = routeFrom;
        this.routeTo = routeTo;
        this.province = province;
    }

    // Getters and setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public String getBusNumber() {
        return busNumber;
    }

    public void setBusNumber(String busNumber) {
        this.busNumber = busNumber;
    }

    public BusType getType() {
        return type;
    }

    public void setType(BusType type) {
        this.type = type;
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

    public BusStatus getStatus() {
        return status;
    }

    public void setStatus(BusStatus status) {
        this.status = status;
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

    public List<OperatorAssignment> getOperatorAssignments() {
        return operatorAssignments;
    }

    public void setOperatorAssignments(List<OperatorAssignment> operatorAssignments) {
        this.operatorAssignments = operatorAssignments;
    }

    public BusWallet getBusWallet() {
        return busWallet;
    }

    public void setBusWallet(BusWallet busWallet) {
        this.busWallet = busWallet;
    }
}