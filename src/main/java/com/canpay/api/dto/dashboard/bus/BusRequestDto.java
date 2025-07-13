package com.canpay.api.dto.dashboard.bus;

import com.canpay.api.entity.Bus.BusType;
import com.canpay.api.entity.Bus.BusStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * DTO for bus creation and update requests.
 */
@Getter
@Setter
@NoArgsConstructor
public class BusRequestDto {
    @NotBlank(message = "Bus number is required")
    @Size(max = 20, message = "Bus number must not exceed 20 characters")
    private String busNumber;

    @NotNull(message = "Bus type is required")
    private BusType type;

    @Size(max = 100, message = "Route from must not exceed 100 characters")
    private String routeFrom;

    @Size(max = 100, message = "Route to must not exceed 100 characters")
    private String routeTo;

    @Size(max = 50, message = "Province must not exceed 50 characters")
    private String province;

    private BusStatus status;

    @NotNull(message = "Owner ID is required")
    private UUID ownerId;

    @NotBlank(message = "Vehicle insurance is required")
    private String vehicleInsurance;

    @NotBlank(message = "Vehicle revenue license is required")
    private String vehicleRevenueLicense;

    public @NotBlank(message = "Bus number is required") @Size(max = 20, message = "Bus number must not exceed 20 characters") String getBusNumber() {
        return busNumber;
    }

    public void setBusNumber(
            @NotBlank(message = "Bus number is required") @Size(max = 20, message = "Bus number must not exceed 20 characters") String busNumber) {
        this.busNumber = busNumber;
    }

    public @NotNull(message = "Bus type is required") BusType getType() {
        return type;
    }

    public void setType(@NotNull(message = "Bus type is required") BusType type) {
        this.type = type;
    }

    public @Size(max = 100, message = "Route from must not exceed 100 characters") String getRouteFrom() {
        return routeFrom;
    }

    public void setRouteFrom(@Size(max = 100, message = "Route from must not exceed 100 characters") String routeFrom) {
        this.routeFrom = routeFrom;
    }

    public @Size(max = 100, message = "Route to must not exceed 100 characters") String getRouteTo() {
        return routeTo;
    }

    public void setRouteTo(@Size(max = 100, message = "Route to must not exceed 100 characters") String routeTo) {
        this.routeTo = routeTo;
    }

    public @Size(max = 50, message = "Province must not exceed 50 characters") String getProvince() {
        return province;
    }

    public void setProvince(@Size(max = 50, message = "Province must not exceed 50 characters") String province) {
        this.province = province;
    }

    public BusStatus getStatus() {
        return status;
    }

    public void setStatus(BusStatus status) {
        this.status = status;
    }

    public @NotNull(message = "Owner ID is required") UUID getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(@NotNull(message = "Owner ID is required") UUID ownerId) {
        this.ownerId = ownerId;
    }

    public @NotBlank(message = "Vehicle insurance is required") String getVehicleInsurance() {
        return vehicleInsurance;
    }

    public void setVehicleInsurance(@NotBlank(message = "Vehicle insurance is required") String vehicleInsurance) {
        this.vehicleInsurance = vehicleInsurance;
    }

    public @NotBlank(message = "Vehicle revenue license is required") String getVehicleRevenueLicense() {
        return vehicleRevenueLicense;
    }

    public void setVehicleRevenueLicense(
            @NotBlank(message = "Vehicle revenue license is required") String vehicleRevenueLicense) {
        this.vehicleRevenueLicense = vehicleRevenueLicense;
    }
}