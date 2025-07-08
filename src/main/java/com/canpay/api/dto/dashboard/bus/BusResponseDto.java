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
}