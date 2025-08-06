package com.canpay.api.controller.account;

import com.canpay.api.dto.dashboard.operatorassignment.OperatorAssignmentListResponseDto;
import com.canpay.api.dto.dashboard.operatorassignment.OperatorAssignmentRequestDto;
import com.canpay.api.dto.dashboard.operatorassignment.OperatorAssignmentResponseDto;
import com.canpay.api.dto.dashboard.operatorassignment.OperatorAssignmentListWithTotalDto;
import com.canpay.api.entity.OperatorAssignment;
import com.canpay.api.entity.ResponseEntityBuilder;
import com.canpay.api.repository.dashboard.DOperatorAssignmentRepository;
import com.canpay.api.service.dashboard.DBusService;
import com.canpay.api.service.implementation.JwtService;
import com.canpay.api.service.dashboard.DOperatorAssignmentService;
import com.canpay.api.service.implementation.UserServiceImpl;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.springframework.dao.DataIntegrityViolationException;
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
    UUID ownerUuid;

    public OperatorAssignmentControllerAc(DOperatorAssignmentRepository operatorAssignmentRepository, JwtService jwtService, UserServiceImpl userService, DOperatorAssignmentService operatorAssignmentService, DBusService busService) {
        this.operatorAssignmentRepository = operatorAssignmentRepository;
        this.jwtService = jwtService;
        this.userService = userService;
        this.operatorAssignmentService = operatorAssignmentService;
        this.busService = busService;
    }

    /**
     * Assigns an operator to a bus.
     *
     * @param authHeader Authorization header containing the Bearer token.
     * @param requestDto Request DTO containing bus ID, operator ID, and status.
     * @return ResponseEntity with the assignment result.
     */
    @PostMapping("/assign-to-bus")
    @PreAuthorize("hasRole('OWNER')")
    @Transactional
    public ResponseEntity<?> assignOperator(
            @RequestHeader(value = "Authorization") String authHeader,
            @Valid @RequestBody OperatorAssignmentRequestDto requestDto) {
        logger.debug("Received operator assignment request: busId={}, operatorId={}, status={}",
                requestDto.getBusId(), requestDto.getOperatorId(), requestDto.getStatus());

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.warn("Authorization header missing or invalid");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "Authorization header with Bearer token is required"));
        }

        String token = authHeader.substring(7);
        String ownerEmail;
        try {
            ownerEmail = jwtService.extractEmail(token);
            String tokenRole = jwtService.extractRole(token);
            if (!"OWNER".equals(tokenRole)) {
                logger.warn("Invalid role in token: {}", tokenRole);
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("success", false, "message", "Only owners can assign operators"));
            }
        } catch (Exception e) {
            logger.warn("Invalid token: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "Invalid or expired token"));
        }

        try {
            // Validate owner
            User owner = userService.findUserByEmailAndRole(ownerEmail, User.UserRole.OWNER)
                    .orElseThrow(() -> {
                        logger.warn("Owner not found for email: {}", ownerEmail);
                        return new RuntimeException("Owner not found");
                    });
            logger.debug("Owner validated: id={}, email={}", owner.getId(), owner.getEmail());

            // Validate operator
            User operator = userService.findUserById(requestDto.getOperatorId())
                    .orElseThrow(() -> {
                        logger.warn("Operator not found for ID: {}", requestDto.getOperatorId());
                        return new RuntimeException("Operator not found");
                    });
            if (!User.UserRole.OPERATOR.equals(operator.getRole())) {
                logger.warn("User is not an operator: {}", requestDto.getOperatorId());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("success", false, "message", "User is not an operator"));
            }
            logger.debug("Operator validated: id={}, role={}", operator.getId(), operator.getRole());

            // Validate bus
            Bus bus = busService.findBusById(requestDto.getBusId());
            if (bus == null) {
                logger.warn("Bus not found for ID: {}", requestDto.getBusId());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("success", false, "message", "Bus not found"));
            }
            if (bus.getOwner() == null || !bus.getOwner().getId().equals(owner.getId())) {
                logger.warn("Bus {} does not belong to owner {}", requestDto.getBusId(), owner.getId());
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("success", false, "message", "Bus does not belong to this owner"));
            }
            logger.debug("Bus validated: id={}, ownerId={}", bus.getId(), bus.getOwner().getId());

            // Check for any existing assignment for this operator and bus (regardless of status)
            if (busService.hasAnyOperatorAssignment(requestDto.getBusId(), requestDto.getOperatorId())) {
                logger.warn("Operator {} already assigned to bus {} (any status)", requestDto.getOperatorId(), requestDto.getBusId());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("success", false, "message", "Operator already assigned to this bus"));
            }

            OperatorAssignment assignment = new OperatorAssignment(operator, bus,
                    requestDto.getStatus() != null ? requestDto.getStatus() : OperatorAssignment.AssignmentStatus.ACTIVE);   // previously this is pending
            logger.debug("OperatorAssignment state: id={}, busId={}, operatorId={}, status={}",
                    assignment.getId(), assignment.getBus().getId(), assignment.getOperator().getId(), assignment.getStatus());

            busService.assignOperator(assignment);

            // Create response DTO
            OperatorAssignmentResponseDto responseDto = new OperatorAssignmentResponseDto();
            responseDto.setId(assignment.getId());
            responseDto.setStatus(assignment.getStatus());
            responseDto.setAssignedAt(assignment.getAssignedAt());
            responseDto.setCreatedAt(assignment.getCreatedAt());
            responseDto.setUpdatedAt(assignment.getUpdatedAt());

            // Populate UserDto
            UserDto userDto = new UserDto();
            userDto.setId(operator.getId());
            userDto.setName(operator.getName());
            userDto.setEmail(operator.getEmail());
            responseDto.setOperator(userDto);

            // Populate BusResponseDto
            BusResponseDto busDto = new BusResponseDto();
            busDto.setId(bus.getId());
            busDto.setBusNumber(bus.getBusNumber());
            busDto.setType(bus.getType());
            busDto.setRouteFrom(bus.getRouteFrom());
            busDto.setRouteTo(bus.getRouteTo());
            busDto.setStatus(bus.getStatus());
            busDto.setOwnerId(bus.getOwner() != null ? bus.getOwner().getId() : null);
            busDto.setOwnerName(bus.getOwner() != null ? bus.getOwner().getName() : null);

            responseDto.setBus(busDto);

            logger.info("Operator {} assigned to bus {} with status {}",
                    requestDto.getOperatorId(), requestDto.getBusId(), requestDto.getStatus());

            return new ResponseEntityBuilder.Builder<Map<String, Object>>()
                    .resultMessage("Operator assigned successfully")
                    .httpStatus(HttpStatus.CREATED)
                    .body(Map.of("data", responseDto))
                    .buildWrapped();
        } catch (DataIntegrityViolationException e) {
            logger.error("Constraint violation while assigning operator: busId={}, operatorId={}, error={}",
                    requestDto.getBusId(), requestDto.getOperatorId(), e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "message", "Database constraint violation: " + e.getMessage()));
        } catch (Exception e) { // Changed from RuntimeException to Exception to catch all potential issues
            logger.error("Failed to assign operator: busId={}, operatorId={}, error={}",
                    requestDto.getBusId(), requestDto.getOperatorId(), e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "message", "Runtime exception: " + e.getMessage()));
        }
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
            @PathVariable String ownerId,
            @RequestParam("status") OperatorAssignment.AssignmentStatus status) {
        ownerUuid = UUID.fromString(ownerId);

        OperatorAssignmentListWithTotalDto result =
                operatorAssignmentService.getOperatorAssignmentsByOwnerIdAndStatus(ownerUuid, status);
        return new ResponseEntityBuilder.Builder<OperatorAssignmentListWithTotalDto>()
                .resultMessage("Operators with status " + status + " assigned to owner's buses")
                .body(result)
                .buildWrapped();
    }


    // total operators assigned to owner by number
    @GetMapping("/{ownerId}/total-operators")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<?> getTotalOperatorsAssignedToOwner(@PathVariable String ownerId) {
        ownerUuid = UUID.fromString(ownerId);
        long totalOperators = operatorAssignmentService.getTotalOperatorsAssignedToOwner(ownerUuid);
        return new ResponseEntityBuilder.Builder<Long>()
                .resultMessage("Total operators assigned to owner")
                .body(totalOperators)
                .buildWrapped();
    }

    @GetMapping("/{ownerId}/active-operators")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<?> getTotalActiveOperatorsAssignedToOwner(@PathVariable String ownerId) {
        ownerUuid = UUID.fromString(ownerId);
        long totalActiveOperators = operatorAssignmentService.countActiveOperatorsByOwnerId(ownerUuid);
        return new ResponseEntityBuilder.Builder<Long>()
                .resultMessage("Total ACTIVE operators assigned to owner's buses")
                .body(totalActiveOperators)
                .buildWrapped();
    }

    @GetMapping("/{ownerId}/active-operators-list")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<?> getActiveOperatorsAssignedToOwner(@PathVariable String ownerId) {
        ownerUuid = UUID.fromString(ownerId);
        List<OperatorAssignmentListResponseDto> activeAssignments =
                operatorAssignmentService.getActiveOperatorAssignmentsByOwnerId(ownerUuid);

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
    public ResponseEntity<?> getOwnerBusesStatusCount(@PathVariable String ownerId) {
        ownerUuid = UUID.fromString(ownerId);

        Map<String, Long> statusCount = busService.countBusesByStatusForOwner(ownerUuid);
        // Get all buses for the owner
        List<BusResponseDto> buses = busService.getBusesByOwner(ownerUuid);

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
    public ResponseEntity<?> getOwnerBuses(@PathVariable String ownerId) {
        ownerUuid = UUID.fromString(ownerId);
        List<BusResponseDto> buses = busService.getBusesByOwner(ownerUuid);
        Map<String, Object> result = new HashMap<>();
        result.put("total", buses.size());
        result.put("buses", buses);
        return new ResponseEntityBuilder.Builder<>()
                .resultMessage("Owner's buses fetched successfully")
                .body(result)
                .buildWrapped();
    }
}
