package com.canpay.api.dto.dashboard.bus;

import com.canpay.api.entity.Bus.BusType;
import com.canpay.api.entity.Bus.BusStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for bus response data.
 */
@Getter
@Setter
@NoArgsConstructor
public class BusResponseDto {

    private UUID id;
    private String busNumber;
    private BusType type;
    private String routeFrom;
    private String routeTo;
    private String province;
    private BusStatus status;
    private UUID ownerId;
    private String ownerName;
    private UUID walletId;
    private String vehicleInsurance;
    private String vehicleRevenueLicense;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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

    public UUID getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(UUID ownerId) {
        this.ownerId = ownerId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public UUID getWalletId() {
        return walletId;
    }

    public void setWalletId(UUID walletId) {
        this.walletId = walletId;
    }

    public String getVehicleInsurance() {
        return vehicleInsurance;
    }

    public void setVehicleInsurance(String vehicleInsurance) {
        this.vehicleInsurance = vehicleInsurance;
    }

    public String getVehicleRevenueLicense() {
        return vehicleRevenueLicense;
    }

    public void setVehicleRevenueLicense(String vehicleRevenueLicense) {
        this.vehicleRevenueLicense = vehicleRevenueLicense;
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
}