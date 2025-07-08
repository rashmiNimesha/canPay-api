package com.canpay.api.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a bus entity in the system.
 */
@Entity
@Table(name = "buses")
@Getter
@Setter
public class Bus extends BaseEntity {
    /** Unique bus number. */
    @Column(name = "bus_number", nullable = false)
    @NotBlank
    @Size(max = 20)
    private String busNumber;

    /** Type of the bus (NORMAL, HIGHWAY, INTERCITY). */
    @Enumerated(EnumType.STRING)
    private BusType type;

    /** Starting point of the bus route. */
    @Column(name = "route_from")
    @Size(max = 100)
    private String routeFrom;

    /** Destination of the bus route. */
    @Column(name = "route_to")
    @Size(max = 100)
    private String routeTo;

    /** Province where the bus operates. */
    @Size(max = 50)
    private String province;

    /** Current status of the bus. */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull
    private BusStatus status = BusStatus.PENDING;

    /** The owner of the bus. */
    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    @JsonBackReference
    @NotNull
    private User owner;

    /** List of operator assignments for this bus. */
    @OneToMany(mappedBy = "bus", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<OperatorAssignment> operatorAssignments = new ArrayList<>();;

    /** Wallet associated with this bus. */
    @OneToOne(mappedBy = "bus", cascade = CascadeType.ALL)
    @JsonManagedReference
    private Wallet wallet;

    // Enums
    public enum BusType {
        NORMAL, HIGHWAY, INTERCITY
    }

    public enum BusStatus {
        PENDING, ACTIVE, INACTIVE, REJECTED, BLOCKED
    }

    // Business Constructor
    public Bus(User owner, String busNumber, BusType type, String routeFrom, String routeTo, String province,
               BusStatus status) {
        this.owner = owner;
        this.busNumber = busNumber;
        this.type = type;
        this.routeFrom = routeFrom;
        this.routeTo = routeTo;
        this.province = province;
        this.status = status;
        this.operatorAssignments = new ArrayList<>(); // Initialize in constructor
    }

    public Bus() {
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

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public List<OperatorAssignment> getOperatorAssignments() {
        return operatorAssignments;
    }

    public void setOperatorAssignments(List<OperatorAssignment> operatorAssignments) {
        this.operatorAssignments = operatorAssignments;
    }

    public Wallet getWallet() {
        return wallet;
    }

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }
}