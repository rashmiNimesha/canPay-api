package com.canpay.api.dto.dashboard.bus;

import com.canpay.api.entity.Bus.BusType;
import com.canpay.api.entity.Bus.BusStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO for bus search filters.
 */
@Getter
@Setter
@NoArgsConstructor
public class BusSearchDto {

    private String busNumber;
    private BusType type;
    private List<BusType> types;
    private BusStatus status;
    private List<BusStatus> statuses;
    private UUID ownerId;
    private String routeFrom;
    private String routeTo;
    private String province;
    private LocalDateTime createdAfter;
    private LocalDateTime createdBefore;
    private LocalDateTime updatedAfter;
    private LocalDateTime updatedBefore;
}