package com.canpay.api.controller.account;

import com.canpay.api.dto.dashboard.operatorassignment.OperatorAssignmentResponseDto;
import com.canpay.api.dto.dashboard.operatorassignment.OperatorAssignmentListWithTotalDto;
import com.canpay.api.entity.OperatorAssignment;
import com.canpay.api.entity.ResponseEntityBuilder;
import com.canpay.api.repository.dashboard.DOperatorAssignmentRepository;
import com.canpay.api.service.implementation.JwtService;
import com.canpay.api.service.dashboard.DOperatorAssignmentService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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

    @Autowired
    private DOperatorAssignmentService operatorAssignmentService;

    public OperatorAssignmentControllerAc(DOperatorAssignmentRepository operatorAssignmentRepository, JwtService jwtService) {
        this.operatorAssignmentRepository = operatorAssignmentRepository;
        this.jwtService = jwtService;
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

}
