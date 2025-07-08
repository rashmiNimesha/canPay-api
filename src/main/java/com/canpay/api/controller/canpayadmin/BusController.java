package com.canpay.api.controller.canpayadmin;

import com.canpay.api.dto.dashboard.bus.BusRequestDto;
import com.canpay.api.dto.dashboard.bus.BusResponseDto;
import com.canpay.api.dto.dashboard.bus.BusSearchDto;
import com.canpay.api.entity.Bus.BusStatus;
import com.canpay.api.entity.Bus.BusType;
import com.canpay.api.service.dashboard.DBusService;
import com.canpay.api.service.dashboard.DBusService.BusStatsDto;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
    public ResponseEntity<BusResponseDto> getBusById(@PathVariable UUID id) {
        try {
            BusResponseDto responseDto = busService.getBusById(id);
            return new ResponseEntity<>(responseDto, HttpStatus.OK);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
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
    public ResponseEntity<List<BusResponseDto>> getAllBuses() {
        List<BusResponseDto> buses = busService.getAllBuses();
        return new ResponseEntity<>(buses, HttpStatus.OK);
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
    public ResponseEntity<BusResponseDto> updateBusStatus(@PathVariable UUID id, @RequestParam BusStatus status) {
        try {
            BusResponseDto responseDto = busService.updateBusStatus(id, status);
            return new ResponseEntity<>(responseDto, HttpStatus.OK);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get bus statistics.
     */
    @GetMapping("/buses/statistics")
    public ResponseEntity<BusStatsDto> getBusStatistics() {
        BusStatsDto stats = busService.getBusStatistics();
        return new ResponseEntity<>(stats, HttpStatus.OK);
    }

    /**
     * Approve bus (set status to ACTIVE).
     */
    @PatchMapping("/buses/{id}/approve")
    public ResponseEntity<BusResponseDto> approveBus(@PathVariable UUID id) {
        try {
            BusResponseDto responseDto = busService.updateBusStatus(id, BusStatus.ACTIVE);
            return new ResponseEntity<>(responseDto, HttpStatus.OK);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Reject bus (set status to REJECTED).
     */
    @PatchMapping("/buses/{id}/reject")
    public ResponseEntity<BusResponseDto> rejectBus(@PathVariable UUID id) {
        try {
            BusResponseDto responseDto = busService.updateBusStatus(id, BusStatus.REJECTED);
            return new ResponseEntity<>(responseDto, HttpStatus.OK);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Block bus (set status to BLOCKED).
     */
    @PatchMapping("/buses/{id}/block")
    public ResponseEntity<BusResponseDto> blockBus(@PathVariable UUID id) {
        try {
            BusResponseDto responseDto = busService.updateBusStatus(id, BusStatus.BLOCKED);
            return new ResponseEntity<>(responseDto, HttpStatus.OK);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Activate bus (set status to ACTIVE).
     */
    @PatchMapping("/buses/{id}/activate")
    public ResponseEntity<BusResponseDto> activateBus(@PathVariable UUID id) {
        try {
            BusResponseDto responseDto = busService.updateBusStatus(id, BusStatus.ACTIVE);
            return new ResponseEntity<>(responseDto, HttpStatus.OK);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Deactivate bus (set status to INACTIVE).
     */
    @PatchMapping("/buses/{id}/deactivate")
    public ResponseEntity<BusResponseDto> deactivateBus(@PathVariable UUID id) {
        try {
            BusResponseDto responseDto = busService.updateBusStatus(id, BusStatus.INACTIVE);
            return new ResponseEntity<>(responseDto, HttpStatus.OK);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}