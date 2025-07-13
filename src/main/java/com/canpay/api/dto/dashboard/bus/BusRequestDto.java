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
}