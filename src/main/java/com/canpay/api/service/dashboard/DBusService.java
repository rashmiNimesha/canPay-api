package com.canpay.api.service.dashboard;

import com.canpay.api.dto.dashboard.bus.BusRequestDto;
import com.canpay.api.dto.dashboard.bus.BusResponseDto;
import com.canpay.api.dto.dashboard.bus.BusSearchDto;
import com.canpay.api.entity.Bus;
import com.canpay.api.entity.Bus.BusStatus;
import com.canpay.api.entity.Bus.BusType;
import com.canpay.api.entity.User;
import com.canpay.api.repository.dashboard.DBusRepository;
import com.canpay.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service class for managing Bus entities.
 * Provides business logic for bus operations.
 */
@Service
@Transactional
public class DBusService {

    @Autowired
    private DBusRepository busRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Create a new bus.
     */
    public BusResponseDto createBus(BusRequestDto requestDto) {
        Optional<User> ownerOpt = userRepository.findById(requestDto.getOwnerId());
        if (!ownerOpt.isPresent()) {
            throw new RuntimeException("Owner not found with ID: " + requestDto.getOwnerId());
        }
        User owner = ownerOpt.get();

        Bus bus = new Bus(
                owner,
                requestDto.getBusNumber(),
                requestDto.getType(),
                requestDto.getRouteFrom(),
                requestDto.getRouteTo(),
                requestDto.getProvince(),
                requestDto.getStatus() != null ? requestDto.getStatus() : BusStatus.PENDING,
                requestDto.getVehicleInsurance(),
                requestDto.getVehicleRevenueLicense());

        Bus savedBus = busRepository.save(bus);
        return convertToResponseDto(savedBus);
    }

    /**
     * Get bus by ID.
     */
    @Transactional(readOnly = true)
    public BusResponseDto getBusById(UUID id) {
        Optional<Bus> busOpt = busRepository.findById(id);
        if (!busOpt.isPresent()) {
            throw new RuntimeException("Bus not found with ID: " + id);
        }
        return convertToResponseDto(busOpt.get());
    }

    /**
     * Get bus by bus number.
     */
    @Transactional(readOnly = true)
    public BusResponseDto getBusByNumber(String busNumber) {
        Optional<Bus> busOpt = busRepository.findByBusNumber(busNumber);
        if (!busOpt.isPresent()) {
            throw new RuntimeException("Bus not found with number: " + busNumber);
        }
        return convertToResponseDto(busOpt.get());
    }

    /**
     * Get all buses.
     */
    @Transactional(readOnly = true)
    public List<BusResponseDto> getAllBuses() {
        return busRepository.findAll().stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Update bus.
     */
    public BusResponseDto updateBus(UUID id, BusRequestDto requestDto) {
        Bus existingBus = busRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Bus not found with ID: " + id));

        if (requestDto.getOwnerId() != null && !requestDto.getOwnerId().equals(existingBus.getOwner().getId())) {
            User newOwner = userRepository.findById(requestDto.getOwnerId())
                    .orElseThrow(
                            () -> new IllegalArgumentException("Owner not found with ID: " + requestDto.getOwnerId()));
            existingBus.setOwner(newOwner);
        }

        if (requestDto.getBusNumber() != null) {
            existingBus.setBusNumber(requestDto.getBusNumber());
        }
        if (requestDto.getType() != null) {
            existingBus.setType(requestDto.getType());
        }
        if (requestDto.getRouteFrom() != null) {
            existingBus.setRouteFrom(requestDto.getRouteFrom());
        }
        if (requestDto.getRouteTo() != null) {
            existingBus.setRouteTo(requestDto.getRouteTo());
        }
        if (requestDto.getProvince() != null) {
            existingBus.setProvince(requestDto.getProvince());
        }
        if (requestDto.getStatus() != null) {
            existingBus.setStatus(requestDto.getStatus());
        }
        if (requestDto.getVehicleInsurance() != null) {
            existingBus.setVehicleInsurance(requestDto.getVehicleInsurance());
        }
        if (requestDto.getVehicleRevenueLicense() != null) {
            existingBus.setVehicleRevenueLicense(requestDto.getVehicleRevenueLicense());
        }

        Bus updatedBus = busRepository.save(existingBus);
        return convertToResponseDto(updatedBus);
    }

    /**
     * Delete bus by ID.
     */
    public void deleteBus(UUID id) {
        if (!busRepository.existsById(id)) {
            throw new IllegalArgumentException("Bus not found with ID: " + id);
        }
        busRepository.deleteById(id);
    }

    /**
     * Search buses based on criteria.
     */
    @Transactional(readOnly = true)
    public List<BusResponseDto> searchBuses(BusSearchDto searchDto) {
        List<Bus> buses;

        if (searchDto.getBusNumber() != null && !searchDto.getBusNumber().isEmpty()) {
            buses = busRepository.findByBusNumberContaining(searchDto.getBusNumber());
        } else if (searchDto.getTypes() != null && !searchDto.getTypes().isEmpty()) {
            buses = busRepository.findByTypeIn(searchDto.getTypes());
        } else if (searchDto.getStatuses() != null && !searchDto.getStatuses().isEmpty()) {
            buses = busRepository.findByStatusIn(searchDto.getStatuses());
        } else if (searchDto.getOwnerId() != null) {
            buses = busRepository.findByOwner_Id(searchDto.getOwnerId());
        } else if (searchDto.getType() != null) {
            buses = busRepository.findByType(searchDto.getType());
        } else if (searchDto.getStatus() != null) {
            buses = busRepository.findByStatus(searchDto.getStatus());
        } else if (searchDto.getRouteFrom() != null && !searchDto.getRouteFrom().isEmpty()) {
            buses = busRepository.findByRouteFromContaining(searchDto.getRouteFrom());
        } else if (searchDto.getRouteTo() != null && !searchDto.getRouteTo().isEmpty()) {
            buses = busRepository.findByRouteToContaining(searchDto.getRouteTo());
        } else if (searchDto.getProvince() != null && !searchDto.getProvince().isEmpty()) {
            buses = busRepository.findByProvinceContaining(searchDto.getProvince());
        } else if (searchDto.getCreatedAfter() != null && searchDto.getCreatedBefore() != null) {
            buses = busRepository.findByCreatedAtBetween(searchDto.getCreatedAfter(), searchDto.getCreatedBefore());
        } else if (searchDto.getUpdatedAfter() != null && searchDto.getUpdatedBefore() != null) {
            buses = busRepository.findByUpdatedAtBetween(searchDto.getUpdatedAfter(), searchDto.getUpdatedBefore());
        } else {
            buses = busRepository.findAll();
        }

        return buses.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Get buses by owner ID.
     */
    @Transactional(readOnly = true)
    public List<BusResponseDto> getBusesByOwner(UUID ownerId) {
        return busRepository.findByOwner_Id(ownerId).stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Get buses by status.
     */
    @Transactional(readOnly = true)
    public List<BusResponseDto> getBusesByStatus(BusStatus status) {
        return busRepository.findByStatus(status).stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Get buses by type.
     */
    @Transactional(readOnly = true)
    public List<BusResponseDto> getBusesByType(BusType type) {
        return busRepository.findByType(type).stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Update bus status.
     */
    public BusResponseDto updateBusStatus(UUID id, BusStatus status) {
        Bus bus = busRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Bus not found with ID: " + id));

        bus.setStatus(status);
        Bus updatedBus = busRepository.save(bus);
        return convertToResponseDto(updatedBus);
    }

    /**
     * Get bus statistics.
     */
    @Transactional(readOnly = true)
    public BusStatsDto getBusStatistics() {
        long totalBuses = busRepository.count();
        long activeBuses = busRepository.countByStatus(BusStatus.ACTIVE);
        long pendingBuses = busRepository.countByStatus(BusStatus.PENDING);
        long inactiveBuses = busRepository.countByStatus(BusStatus.INACTIVE);
        long rejectedBuses = busRepository.countByStatus(BusStatus.REJECTED);
        long blockedBuses = busRepository.countByStatus(BusStatus.BLOCKED);

        long normalBuses = busRepository.countByType(BusType.NORMAL);
        long highwayBuses = busRepository.countByType(BusType.HIGHWAY);
        long intercityBuses = busRepository.countByType(BusType.INTERCITY);

        return new BusStatsDto(totalBuses, activeBuses, pendingBuses, inactiveBuses,
                rejectedBuses, blockedBuses, normalBuses, highwayBuses, intercityBuses);
    }

    /**
     * Convert Bus entity to BusResponseDto.
     */
    private BusResponseDto convertToResponseDto(Bus bus) {
        BusResponseDto dto = new BusResponseDto();
        dto.setId(bus.getId());
        dto.setBusNumber(bus.getBusNumber());
        dto.setType(bus.getType());
        dto.setRouteFrom(bus.getRouteFrom());
        dto.setRouteTo(bus.getRouteTo());
        dto.setProvince(bus.getProvince());
        dto.setStatus(bus.getStatus());
        dto.setOwnerId(bus.getOwner().getId());
        dto.setOwnerName(bus.getOwner().getName());
        dto.setWalletId(bus.getWallet() != null ? bus.getWallet().getId() : null);
        dto.setVehicleInsurance(bus.getVehicleInsurance());
        dto.setVehicleRevenueLicense(bus.getVehicleRevenueLicense());
        dto.setCreatedAt(bus.getCreatedAt());
        dto.setUpdatedAt(bus.getUpdatedAt());
        return dto;
    }

    /**
     * Inner class for bus statistics.
     */
    public static class BusStatsDto {
        private long totalBuses;
        private long activeBuses;
        private long pendingBuses;
        private long inactiveBuses;
        private long rejectedBuses;
        private long blockedBuses;
        private long normalBuses;
        private long highwayBuses;
        private long intercityBuses;

        public BusStatsDto(long totalBuses, long activeBuses, long pendingBuses, long inactiveBuses,
                long rejectedBuses, long blockedBuses, long normalBuses, long highwayBuses, long intercityBuses) {
            this.totalBuses = totalBuses;
            this.activeBuses = activeBuses;
            this.pendingBuses = pendingBuses;
            this.inactiveBuses = inactiveBuses;
            this.rejectedBuses = rejectedBuses;
            this.blockedBuses = blockedBuses;
            this.normalBuses = normalBuses;
            this.highwayBuses = highwayBuses;
            this.intercityBuses = intercityBuses;
        }

        // Getters
        public long getTotalBuses() {
            return totalBuses;
        }

        public long getActiveBuses() {
            return activeBuses;
        }

        public long getPendingBuses() {
            return pendingBuses;
        }

        public long getInactiveBuses() {
            return inactiveBuses;
        }

        public long getRejectedBuses() {
            return rejectedBuses;
        }

        public long getBlockedBuses() {
            return blockedBuses;
        }

        public long getNormalBuses() {
            return normalBuses;
        }

        public long getHighwayBuses() {
            return highwayBuses;
        }

        public long getIntercityBuses() {
            return intercityBuses;
        }
    }
}