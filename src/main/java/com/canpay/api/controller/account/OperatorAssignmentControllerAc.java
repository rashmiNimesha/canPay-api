package com.canpay.api.controller.account;

import com.canpay.api.dto.dashboard.operatorassignment.OperatorAssignmentResponseDto;
import com.canpay.api.entity.OperatorAssignment;
import com.canpay.api.repository.OperatorAssignmentRepository;
import com.canpay.api.service.implementation.JwtService;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;


@RestController
@RequestMapping("/api/v1/operator")
public class OperatorAssignmentControllerAc {

    private final Logger logger = org.slf4j.LoggerFactory.getLogger(OperatorAssignmentControllerAc.class);

    private final OperatorAssignmentRepository operatorAssignmentRepository;
    private final JwtService jwtService;

    public OperatorAssignmentControllerAc(OperatorAssignmentRepository operatorAssignmentRepository, JwtService jwtService) {
        this.operatorAssignmentRepository = operatorAssignmentRepository;
        this.jwtService = jwtService;
    }

    @GetMapping("/assignment-status/{operatorId}")
    @PreAuthorize("hasRole('OPERATOR')")
    public ResponseEntity<?> getAssignmentStatus(@PathVariable String operatorId,  @RequestHeader(value = "Authorization") String authHeader) {

        logger.info("Received request to get assignment status for operatorId: {}", operatorId);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.warn("Authorization header missing or invalid");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "Authorization header with Bearer token is required"));
        }

        String token = authHeader.substring(7);

        try {
            String tokenRole = jwtService.extractRole(token);
            if (!"OPERATOR".equals(tokenRole)) {
                logger.warn("Invalid role in token: {}", tokenRole);
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("success", false, "message", "Invalid role for operator assignment check"));
            }
        } catch (Exception e) {
            logger.warn("Invalid token: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "Invalid or expired token"));
        }

        UUID operatorUUId;
        try {
            operatorUUId = UUID.fromString(operatorId);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "message", "Invalid bus ID format: " + operatorId));
        }

        OperatorAssignment assignment = operatorAssignmentRepository
                .findFirstByOperatorIdOrderByAssignedAtDesc(operatorUUId);

        OperatorAssignmentResponseDto responseDto = new OperatorAssignmentResponseDto();

        if (assignment == null) {
            responseDto.setAssigned(false);
            responseDto.setStatus(null);
            responseDto.setBus(null);
            responseDto.setAssignedAt(null);
            return ResponseEntity.ok(responseDto);
        }

        boolean assigned = assignment.getStatus() == OperatorAssignment.AssignmentStatus.ACTIVE;
        responseDto.setAssigned(assigned);
        responseDto.setStatus(assignment.getStatus());
        // You may need to map Bus and User to their respective DTOs
        // For now, set only the bus id if you don't have a mapper
        responseDto.setBusId(assignment.getBus().getId());
        responseDto.setOperatorId(assignment.getOperator().getId());
        responseDto.setAssignedAt(assignment.getAssignedAt());
        responseDto.setId(assignment.getId());
        responseDto.setCreatedAt(assignment.getCreatedAt());
        responseDto.setUpdatedAt(assignment.getUpdatedAt());

        logger.info("Assignment status for operatorId {}: {}", operatorId, responseDto);
        return ResponseEntity.ok(responseDto);

}}
