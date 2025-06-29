package com.canpay.api.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "buses")
@Getter
@Setter
@NoArgsConstructor
public class Bus extends BaseEntity {

    @Column(name = "bus_number", nullable = false)
    @NotBlank
    @Size(max = 20)
    private String busNumber;

    @Enumerated(EnumType.STRING)
    private BusType type;

    @Column(name = "route_from")
    @Size(max = 100)
    private String routeFrom;

    @Column(name = "route_to")
    @Size(max = 100)
    private String routeTo;

    @Size(max = 50)
    private String province;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull
    private BusStatus status = BusStatus.PENDING;

    // Relationships
    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    @JsonBackReference
    @NotNull
    private User owner;

    @OneToMany(mappedBy = "bus", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<OperatorAssignment> operatorAssignments;

    @OneToOne(mappedBy = "bus", cascade = CascadeType.ALL)
    @JsonManagedReference
    private BusWallet busWallet;

    // Enums
    public enum BusType {
        NORMAL, SEMI_LUXURY, LUXURY, AC
    }

    public enum BusStatus {
        PENDING, APPROVED, REJECTED, BLOCKED, ACTIVE, INACTIVE
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
    }
}