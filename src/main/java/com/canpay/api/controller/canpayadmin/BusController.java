package com.canpay.api.controller.canpayadmin;

import com.canpay.api.dto.dashboard.bus.BusRequestDto;
import com.canpay.api.dto.dashboard.bus.BusResponseDto;
import com.canpay.api.dto.dashboard.bus.BusSearchDto;
import com.canpay.api.entity.ResponseEntityBuilder;
import com.canpay.api.entity.Bus.BusStatus;
import com.canpay.api.entity.Bus.BusType;
import com.canpay.api.entity.Bus;
import com.canpay.api.service.dashboard.DBusService;
import com.canpay.api.service.dashboard.DBusService.BusStatsDto;
import com.canpay.api.service.dashboard.DWalletService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * REST controller for managing buses.
 * Provides endpoints for bus CRUD operations and statistics.
 */
@RestController
@RequestMapping("/api/v1/canpay-admin")
@CrossOrigin(origins = "*", maxAge = 3600)
public class BusController {

    @Autowired
    private DBusService busService;

    @Autowired
    private DWalletService walletService;

    /**
     * Create a new bus.
     */
    @PostMapping("/buses")
    public ResponseEntity<BusResponseDto> createBus(@Valid @RequestBody BusRequestDto requestDto) {
        try {
            BusResponseDto responseDto = busService.createBus(requestDto);
            return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get bus by ID.
     */
    @GetMapping("/buses/{id}")
    public ResponseEntity<?> getBusById(@PathVariable UUID id) {
        try {
            BusResponseDto responseDto = busService.getBusById(id);
            return new ResponseEntityBuilder.Builder<Map<String, Object>>()
                    .resultMessage("Bus details retrieved successfully")
                    .httpStatus(HttpStatus.OK)
                    .body(Map.of("bus", responseDto))
                    .buildWrapped();
        } catch (RuntimeException e) {
            return new ResponseEntityBuilder.Builder<Map<String, Object>>()
                    .resultMessage("Bus not found")
                    .httpStatus(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()))
                    .buildWrapped();
        }
    }

    /**
     * Get bus by bus number.
     */
    @GetMapping("/buses/number/{busNumber}")
    public ResponseEntity<BusResponseDto> getBusByNumber(@PathVariable String busNumber) {
        try {
            BusResponseDto responseDto = busService.getBusByNumber(busNumber);
            return new ResponseEntity<>(responseDto, HttpStatus.OK);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get all buses.
     */
    @GetMapping("/buses")
    public ResponseEntity<?> getAllBuses() {
        List<BusResponseDto> buses = busService.getAllBuses();
        return new ResponseEntityBuilder.Builder<List<BusResponseDto>>()
                .resultMessage("List of all buses retrieved successfully")
                .httpStatus(HttpStatus.OK)
                .body(buses)
                .buildWrapped();
    }

    /**
     * Update bus.
     */
    @PutMapping("/buses/{id}")
    public ResponseEntity<BusResponseDto> updateBus(@PathVariable UUID id,
            @Valid @RequestBody BusRequestDto requestDto) {
        try {
            BusResponseDto responseDto = busService.updateBus(id, requestDto);
            return new ResponseEntity<>(responseDto, HttpStatus.OK);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Delete bus.
     */
    @DeleteMapping("/buses/{id}")
    public ResponseEntity<Void> deleteBus(@PathVariable UUID id) {
        try {
            busService.deleteBus(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Search buses.
     */
    @PostMapping("/buses/search")
    public ResponseEntity<List<BusResponseDto>> searchBuses(@RequestBody BusSearchDto searchDto) {
        List<BusResponseDto> buses = busService.searchBuses(searchDto);
        return new ResponseEntity<>(buses, HttpStatus.OK);
    }

    /**
     * Get buses by owner.
     */
    @GetMapping("/buses/owner/{ownerId}")
    public ResponseEntity<List<BusResponseDto>> getBusesByOwner(@PathVariable UUID ownerId) {
        List<BusResponseDto> buses = busService.getBusesByOwner(ownerId);
        return new ResponseEntity<>(buses, HttpStatus.OK);
    }

    /**
     * Get buses by status.
     */
    @GetMapping("/buses/status/{status}")
    public ResponseEntity<List<BusResponseDto>> getBusesByStatus(@PathVariable BusStatus status) {
        List<BusResponseDto> buses = busService.getBusesByStatus(status);
        return new ResponseEntity<>(buses, HttpStatus.OK);
    }

    /**
     * Get buses by type.
     */
    @GetMapping("/buses/type/{type}")
    public ResponseEntity<List<BusResponseDto>> getBusesByType(@PathVariable BusType type) {
        List<BusResponseDto> buses = busService.getBusesByType(type);
        return new ResponseEntity<>(buses, HttpStatus.OK);
    }

    /**
     * Update bus status.
     */
    @PatchMapping("/buses/{id}/status")
    public ResponseEntity<?> updateBusStatus(@PathVariable UUID id, @RequestParam BusStatus status) {
        try {
            BusResponseDto responseDto = busService.updateBusStatus(id, status);

            // If status is being changed to ACTIVE, ensure bus has a wallet (only once)
            if (status == BusStatus.ACTIVE) {
                Bus bus = busService.findBusById(id);
                if (bus != null && bus.getWallet() == null) {
                    // Create wallet for bus when it becomes active for the first time
                    walletService.createBusWallet(bus);
                }
            }

            return new ResponseEntityBuilder.Builder<Map<String, Object>>()
                    .resultMessage("Bus status updated successfully")
                    .httpStatus(HttpStatus.OK)
                    .body(Map.of("bus", responseDto))
                    .buildWrapped();
        } catch (RuntimeException e) {
            return new ResponseEntityBuilder.Builder<Map<String, Object>>()
                    .resultMessage("Bus not found")
                    .httpStatus(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()))
                    .buildWrapped();
        }
    }

    /**
     * Get bus statistics.
     */
    @GetMapping("/buses/statistics")
    public ResponseEntity<?> getBusStatistics() {
        BusStatsDto stats = busService.getBusStatistics();
        return new ResponseEntityBuilder.Builder<Map<String, Object>>()
                .resultMessage("Bus statistics retrieved successfully")
                .httpStatus(HttpStatus.OK)
                .body(Map.of("statistics", stats))
                .buildWrapped();
    }

    /**
     * Retrieves the total count of buses.
     * 
     * @return response entity with bus count
     */
    @GetMapping("/buses/count")
    public ResponseEntity<?> getBusCount() {
        long countTotal = busService.getTotalBusCount();
        long countActive = busService.getBusCountByStatus(BusStatus.ACTIVE);
        long countPending = busService.getBusCountByStatus(BusStatus.PENDING);
        long countInactive = busService.getBusCountByStatus(BusStatus.INACTIVE);
        long countRejected = busService.getBusCountByStatus(BusStatus.REJECTED);
        long countBlocked = busService.getBusCountByStatus(BusStatus.BLOCKED);

        return new ResponseEntityBuilder.Builder<Map<String, Object>>()
                .resultMessage("Total number of buses retrieved successfully")
                .httpStatus(HttpStatus.OK)
                .body(Map.of(
                        "total", countTotal,
                        "active", countActive,
                        "pending", countPending,
                        "inactive", countInactive,
                        "rejected", countRejected,
                        "blocked", countBlocked))
                .buildWrapped();
    }

    /**
     * Generate QR code data for a bus.
     * 
     * @param id the UUID of the bus
     * @return response entity with QR code data
     */
    @GetMapping("/buses/{id}/qr-code")
    public ResponseEntity<?> getBusQrCode(@PathVariable UUID id) {
        try {
            Map<String, String> qrData = busService.generateBusQrCodeData(id);
            return new ResponseEntityBuilder.Builder<Map<String, Object>>()
                    .resultMessage("Bus QR code data generated successfully")
                    .httpStatus(HttpStatus.OK)
                    .body(Map.of("qrData", qrData))
                    .buildWrapped();
        } catch (RuntimeException e) {
            return new ResponseEntityBuilder.Builder<Map<String, Object>>()
                    .resultMessage("Bus not found")
                    .httpStatus(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()))
                    .buildWrapped();
        }
    }
}