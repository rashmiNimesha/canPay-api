package com.canpay.api.controller.account;

import com.canpay.api.dto.dashboard.operatorassignment.OperatorAssignmentListResponseDto;
import com.canpay.api.dto.dashboard.operatorassignment.OperatorAssignmentResponseDto;
import com.canpay.api.dto.dashboard.operatorassignment.OperatorAssignmentListWithTotalDto;
import com.canpay.api.entity.OperatorAssignment;
import com.canpay.api.entity.ResponseEntityBuilder;
import com.canpay.api.repository.dashboard.DOperatorAssignmentRepository;
import com.canpay.api.service.dashboard.DBusService;
import com.canpay.api.service.implementation.JwtService;
import com.canpay.api.service.dashboard.DOperatorAssignmentService;
import com.canpay.api.service.implementation.UserServiceImpl;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.canpay.api.entity.Bus;
import com.canpay.api.entity.User;
import com.canpay.api.dto.dashboard.bus.BusResponseDto;
import com.canpay.api.dto.dashboard.user.UserDto;


@RestController
@RequestMapping("/api/v1/operator-assignment")     // operator-assignment change
public class OperatorAssignmentControllerAc {

    private final Logger logger = org.slf4j.LoggerFactory.getLogger(OperatorAssignmentControllerAc.class);

    private final DOperatorAssignmentRepository operatorAssignmentRepository;
    private final JwtService jwtService;
    public final UserServiceImpl userService;
    private final DOperatorAssignmentService operatorAssignmentService;
    private final DBusService busService;

    public OperatorAssignmentControllerAc(DOperatorAssignmentRepository operatorAssignmentRepository, JwtService jwtService, UserServiceImpl userService, DOperatorAssignmentService operatorAssignmentService, DBusService busService) {
        this.operatorAssignmentRepository = operatorAssignmentRepository;
        this.jwtService = jwtService;
        this.userService = userService;
        this.operatorAssignmentService = operatorAssignmentService;
        this.busService = busService;
    }

    @GetMapping("/assignment-status/{operatorId}")
    @PreAuthorize("hasRole('OPERATOR')")
    public ResponseEntity<?> getAssignmentStatus(
            @PathVariable String operatorId,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        logger.info("Received request to get assignment status for operatorId: {}", operatorId);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.warn("Authorization header missing or invalid");
            return new ResponseEntityBuilder.Builder<>()
                    .resultMessage("Authorization header with Bearer token is required")
                    .httpStatus(HttpStatus.UNAUTHORIZED)
                    .buildWrapped();
        }

        String token = authHeader.substring(7);

        try {
            String tokenRole = jwtService.extractRole(token);
            if (!"OPERATOR".equals(tokenRole)) {
                logger.warn("Invalid role in token: {}", tokenRole);
                return new ResponseEntityBuilder.Builder<>()
                        .resultMessage("Invalid role for operator assignment check")
                        .httpStatus(HttpStatus.FORBIDDEN)
                        .buildWrapped();
            }
        } catch (Exception e) {
            logger.warn("Invalid token: {}", e.getMessage());
            return new ResponseEntityBuilder.Builder<>()
                    .resultMessage("Invalid or expired token")
                    .httpStatus(HttpStatus.UNAUTHORIZED)
                    .buildWrapped();
        }

        UUID operatorUUId;
        try {
            operatorUUId = UUID.fromString(operatorId);
        } catch (IllegalArgumentException e) {
            return new ResponseEntityBuilder.Builder<>()
                    .resultMessage("Invalid operator ID format: " + operatorId)
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .buildWrapped();
        }

        OperatorAssignment assignment = operatorAssignmentRepository
                .findFirstByOperatorIdOrderByAssignedAtDesc(operatorUUId);

        OperatorAssignmentResponseDto responseDto = new OperatorAssignmentResponseDto();

        if (assignment == null) {
            responseDto.setAssigned(false);
            responseDto.setStatus(null);
            responseDto.setBus(null);
            responseDto.setAssignedAt(null);
            return new ResponseEntityBuilder.Builder<OperatorAssignmentResponseDto>()
                    .resultMessage("No assignment found for operator")
                    .body(responseDto)
                    .httpStatus(HttpStatus.OK)
                    .buildWrapped();
        }

        boolean assigned = assignment.getStatus() == OperatorAssignment.AssignmentStatus.ACTIVE;
        responseDto.setAssigned(assigned);
        responseDto.setStatus(assignment.getStatus());

        Bus bus = assignment.getBus();
        BusResponseDto busDto = null;
        if (bus != null) {
            busDto = new BusResponseDto();
            busDto.setId(bus.getId());
            busDto.setBusNumber(bus.getBusNumber());
            busDto.setType(bus.getType());
            busDto.setRouteFrom(bus.getRouteFrom());
            busDto.setRouteTo(bus.getRouteTo());
            busDto.setStatus(bus.getStatus());
            busDto.setCreatedAt(bus.getCreatedAt());
            busDto.setUpdatedAt(bus.getUpdatedAt());
        }
        responseDto.setBus(busDto);

        User operator = assignment.getOperator();
        UserDto operatorDto = null;
        if (operator != null) {
            operatorDto = new UserDto();
            operatorDto.setId(operator.getId());
            operatorDto.setName(operator.getName());
            operatorDto.setEmail(operator.getEmail());
        }
        responseDto.setOperator(operatorDto);

        responseDto.setAssignedAt(assignment.getAssignedAt());
        responseDto.setId(assignment.getId());
        responseDto.setCreatedAt(assignment.getCreatedAt());
        responseDto.setUpdatedAt(assignment.getUpdatedAt());

        logger.info("Assignment status for operatorId {}: {}", operatorId, responseDto);
        return new ResponseEntityBuilder.Builder<OperatorAssignmentResponseDto>()
                .resultMessage("Assignment status fetched successfully")
                .body(responseDto)
                .httpStatus(HttpStatus.OK)
                .buildWrapped();
    }

    // everyone get once
    @GetMapping("/owner/{ownerId}/operators")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<?> getOperatorsByOwnerAndStatus(
            @PathVariable UUID ownerId,
            @RequestParam("status") OperatorAssignment.AssignmentStatus status) {
        OperatorAssignmentListWithTotalDto result =
                operatorAssignmentService.getOperatorAssignmentsByOwnerIdAndStatus(ownerId, status);
        return new ResponseEntityBuilder.Builder<OperatorAssignmentListWithTotalDto>()
                .resultMessage("Operators with status " + status + " assigned to owner's buses")
                .body(result)
                .buildWrapped();
    }


    @GetMapping("/{ownerId}/total-operators")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<?> getTotalOperatorsAssignedToOwner(@PathVariable UUID ownerId) {
        long totalOperators = operatorAssignmentService.getTotalOperatorsAssignedToOwner(ownerId);
        return new ResponseEntityBuilder.Builder<Long>()
                .resultMessage("Total operators assigned to owner")
                .body(totalOperators)
                .buildWrapped();
    }

    @GetMapping("/{ownerId}/active-operators")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<?> getTotalActiveOperatorsAssignedToOwner(@PathVariable UUID ownerId) {
        long totalActiveOperators = operatorAssignmentService.countActiveOperatorsByOwnerId(ownerId);
        return new ResponseEntityBuilder.Builder<Long>()
                .resultMessage("Total ACTIVE operators assigned to owner's buses")
                .body(totalActiveOperators)
                .buildWrapped();
    }

    @GetMapping("/{ownerId}/active-operators-list")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<?> getActiveOperatorsAssignedToOwner(@PathVariable UUID ownerId) {
        List<OperatorAssignmentListResponseDto> activeAssignments =
                operatorAssignmentService.getActiveOperatorAssignmentsByOwnerId(ownerId);

        List<OperatorAssignmentListResponseDto> result = activeAssignments.stream().map(assignment -> {
            var operator = userService.findUserById(assignment.getOperatorId()).orElse(null);
            var bus = busService.findBusById(assignment.getBusId());
            var wallet = bus != null ? bus.getWallet() : null;

            OperatorAssignmentListResponseDto dto = new OperatorAssignmentListResponseDto();
            dto.setOperatorId(assignment.getOperatorId());
            dto.setOperatorName(assignment.getOperatorName());
            dto.setOperatorEmail(operator != null ? operator.getEmail() : null);
            dto.setBusId(assignment.getBusId());
            dto.setBusNumber(assignment.getBusNumber());
            dto.setBusRouteFrom(bus != null ? bus.getRouteFrom() : null);
            dto.setBusRouteTo(bus != null ? bus.getRouteTo() : null);
//            dto.setBusWalletBalance(wallet != null && wallet.getBalance() != null ? wallet.getBalance() : null);
            dto.setStatus(assignment.getStatus());
            dto.setBusOwnerName(
                    bus != null && bus.getOwner() != null ? bus.getOwner().getName() : null);
            return dto;
        }).toList();

        return new ResponseEntityBuilder.Builder<List<OperatorAssignmentListResponseDto>>()
                .resultMessage("List of ACTIVE operators assigned to owner's buses")
                .body(result)
                .buildWrapped();
    }

    // get buses count by status and list of buses by status
    @GetMapping("owner/{ownerId}/buses/status-list")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<?> getOwnerBusesStatusCount(@PathVariable UUID ownerId) {
        Map<String, Long> statusCount = busService.countBusesByStatusForOwner(ownerId);
        // Get all buses for the owner
        List<BusResponseDto> buses = busService.getBusesByOwner(ownerId);

        // Group buses by status
        Map<String, List<BusResponseDto>> busesByStatus = new HashMap<>();
        for (BusResponseDto bus : buses) {
            String status = bus.getStatus() != null ? bus.getStatus().toString() : "UNKNOWN";
            busesByStatus.computeIfAbsent(status, k -> new java.util.ArrayList<>()).add(bus);
        }

        // Prepare combined result
        Map<String, Object> result = new HashMap<>();
        result.put("count", statusCount);
        result.put("buses", busesByStatus);

        return new ResponseEntityBuilder.Builder<>()
                .resultMessage("Owner's buses count and list by status fetched successfully")
                .body(result)
                .buildWrapped();
    }

    // get total  and total bus list for owner
    @GetMapping("/owner/{ownerId}/buses/list")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<?> getOwnerBuses(@PathVariable UUID ownerId) {
        List<BusResponseDto> buses = busService.getBusesByOwner(ownerId);
        Map<String, Object> result = new HashMap<>();
        result.put("total", buses.size());
        result.put("buses", buses);
        return new ResponseEntityBuilder.Builder<>()
                .resultMessage("Owner's buses fetched successfully")
                .body(result)
                .buildWrapped();
    }
}
