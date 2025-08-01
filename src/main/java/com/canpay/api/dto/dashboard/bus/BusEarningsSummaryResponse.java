package com.canpay.api.dto.dashboard.bus;

import java.math.BigDecimal;
import java.util.List;

public class BusEarningsSummaryResponse {

    private List<BusWalletSummaryDto> buses;
    private BigDecimal totalEarnings;

    public BusEarningsSummaryResponse(List<BusWalletSummaryDto> buses, BigDecimal totalEarnings) {
        this.buses = buses;
        this.totalEarnings = totalEarnings;
    }

    // Getters and setters
    public List<BusWalletSummaryDto> getBuses() {
        return buses;
    }

    public void setBuses(List<BusWalletSummaryDto> buses) {
        this.buses = buses;
    }

    public BigDecimal getTotalEarnings() {
        return totalEarnings;
    }

    public void setTotalEarnings(BigDecimal totalEarnings) {
        this.totalEarnings = totalEarnings;
    }
}
