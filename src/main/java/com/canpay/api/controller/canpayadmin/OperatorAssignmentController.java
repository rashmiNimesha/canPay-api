package com.canpay.api.controller.canpayadmin;

import com.canpay.api.dto.dashboard.operatorassignment.OperatorAssignmentRequestDto;
import com.canpay.api.dto.dashboard.operatorassignment.OperatorAssignmentListResponseDto;
import com.canpay.api.dto.dashboard.operatorassignment.OperatorAssignmentResponseDto;
import com.canpay.api.dto.dashboard.operatorassignment.OperatorAssignmentSearchDto;
import com.canpay.api.entity.ResponseEntityBuilder;
import com.canpay.api.entity.OperatorAssignment.AssignmentStatus;
import com.canpay.api.service.dashboard.DOperatorAssignmentService;
import com.canpay.api.service.dashboard.DOperatorAssignmentService.AssignmentStatsDto;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * REST controller for managing operator assignments.
 * Provides endpoints for operator assignment CRUD operations and statistics.
 */
@RestController
@RequestMapping("/api/v1/canpay-admin")
@CrossOrigin(origins = "*", maxAge = 3600)
public class OperatorAssignmentController {

    @Autowired
    private DOperatorAssignmentService operatorAssignmentService;

    /**
     * Create a new operator assignment.
     */
    @PostMapping("/operator-assignments")
    public ResponseEntity<?> createAssignment(
            @Valid @RequestBody OperatorAssignmentRequestDto requestDto) {
        try {
            OperatorAssignmentResponseDto responseDto = operatorAssignmentService.createAssignment(requestDto);
            return new ResponseEntityBuilder.Builder<Map<String, Object>>()
                    .resultMessage("Operator assignment created successfully")
                    .httpStatus(HttpStatus.CREATED)
                    .body(Map.of("assignmentId", responseDto.getId()))
                    .buildWrapped();
        } catch (RuntimeException e) {
            return new ResponseEntityBuilder.Builder<Map<String, Object>>()
                    .resultMessage("Failed to create assignment")
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()))
                    .buildWrapped();
        }
    }

    /**
     * Get assignment by ID.
     */
    @GetMapping("/operator-assignments/{id}")
    public ResponseEntity<?> getAssignmentById(@PathVariable UUID id) {
        try {
            OperatorAssignmentResponseDto responseDto = operatorAssignmentService.getAssignmentById(id);
            return new ResponseEntityBuilder.Builder<Map<String, Object>>()
                    .resultMessage("Assignment details retrieved successfully")
                    .httpStatus(HttpStatus.OK)
                    .body(Map.of("assignment", responseDto))
                    .buildWrapped();
        } catch (RuntimeException e) {
            return new ResponseEntityBuilder.Builder<Map<String, Object>>()
                    .resultMessage("Assignment not found")
                    .httpStatus(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()))
                    .buildWrapped();
        }
    }

    /**
     * Get all assignments.
     */
    @GetMapping("/operator-assignments")
    public ResponseEntity<?> getAllAssignments() {
        List<OperatorAssignmentListResponseDto> assignments = operatorAssignmentService.getAllAssignments();
        return new ResponseEntityBuilder.Builder<List<OperatorAssignmentListResponseDto>>()
                .resultMessage("List of all operator assignments retrieved successfully")
                .httpStatus(HttpStatus.OK)
                .body(assignments)
                .buildWrapped();
    }

    /**
     * Update assignment.
     */
    @PutMapping("/operator-assignments/{id}")
    public ResponseEntity<?> updateAssignment(@PathVariable UUID id,
            @Valid @RequestBody OperatorAssignmentRequestDto requestDto) {
        try {
            OperatorAssignmentResponseDto responseDto = operatorAssignmentService.updateAssignment(id, requestDto);
            return new ResponseEntityBuilder.Builder<Map<String, Object>>()
                    .resultMessage("Assignment updated successfully")
                    .httpStatus(HttpStatus.OK)
                    .body(Map.of("assignment", responseDto))
                    .buildWrapped();
        } catch (RuntimeException e) {
            return new ResponseEntityBuilder.Builder<Map<String, Object>>()
                    .resultMessage("Assignment not found")
                    .httpStatus(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()))
                    .buildWrapped();
        }
    }

    /**
     * Delete assignment.
     */
    @DeleteMapping("/operator-assignments/{id}")
    public ResponseEntity<?> deleteAssignment(@PathVariable UUID id) {
        try {
            operatorAssignmentService.deleteAssignment(id);
            return new ResponseEntityBuilder.Builder<Map<String, Object>>()
                    .resultMessage("Assignment deleted successfully")
                    .httpStatus(HttpStatus.OK)
                    .body(Map.of("deleted", true))
                    .buildWrapped();
        } catch (RuntimeException e) {
            return new ResponseEntityBuilder.Builder<Map<String, Object>>()
                    .resultMessage("Assignment not found")
                    .httpStatus(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()))
                    .buildWrapped();
        }
    }

    /**
     * Search assignments based on criteria.
     */
    @PostMapping("/operator-assignments/search")
    public ResponseEntity<?> searchAssignments(
            @RequestBody OperatorAssignmentSearchDto searchDto) {
        List<OperatorAssignmentListResponseDto> assignments = operatorAssignmentService.searchAssignments(searchDto);
        return new ResponseEntityBuilder.Builder<List<OperatorAssignmentListResponseDto>>()
                .resultMessage("Assignment search completed successfully")
                .httpStatus(HttpStatus.OK)
                .body(assignments)
                .buildWrapped();
    }

    /**
     * Get assignments by operator ID.
     */
    @GetMapping("/operator-assignments/operator/{operatorId}")
    public ResponseEntity<?> getAssignmentsByOperator(@PathVariable UUID operatorId) {
        List<OperatorAssignmentListResponseDto> assignments = operatorAssignmentService
                .getAssignmentsByOperator(operatorId);
        return new ResponseEntityBuilder.Builder<List<OperatorAssignmentListResponseDto>>()
                .resultMessage("Assignments by operator retrieved successfully")
                .httpStatus(HttpStatus.OK)
                .body(assignments)
                .buildWrapped();
    }

    /**
     * Get assignments by bus ID.
     */
    @GetMapping("/operator-assignments/bus/{busId}")
    public ResponseEntity<?> getAssignmentsByBus(@PathVariable UUID busId) {
        List<OperatorAssignmentListResponseDto> assignments = operatorAssignmentService.getAssignmentsByBus(busId);
        return new ResponseEntityBuilder.Builder<List<OperatorAssignmentListResponseDto>>()
                .resultMessage("Assignments by bus retrieved successfully")
                .httpStatus(HttpStatus.OK)
                .body(assignments)
                .buildWrapped();
    }

    /**
     * Get assignments by status.
     */
    @GetMapping("/operator-assignments/status/{status}")
    public ResponseEntity<?> getAssignmentsByStatus(@PathVariable AssignmentStatus status) {
        List<OperatorAssignmentListResponseDto> assignments = operatorAssignmentService.getAssignmentsByStatus(status);
        return new ResponseEntityBuilder.Builder<List<OperatorAssignmentListResponseDto>>()
                .resultMessage("Assignments by status retrieved successfully")
                .httpStatus(HttpStatus.OK)
                .body(assignments)
                .buildWrapped();
    }

    /**
     * Get assignments by bus owner ID.
     */
    @GetMapping("/operator-assignments/bus-owner/{ownerId}")
    public ResponseEntity<?> getAssignmentsByBusOwner(@PathVariable UUID ownerId) {
        List<OperatorAssignmentListResponseDto> assignments = operatorAssignmentService
                .getAssignmentsByBusOwner(ownerId);
        return new ResponseEntityBuilder.Builder<List<OperatorAssignmentListResponseDto>>()
                .resultMessage("Assignments by bus owner retrieved successfully")
                .httpStatus(HttpStatus.OK)
                .body(assignments)
                .buildWrapped();
    }

    /**
     * Update assignment status.
     */
    @PatchMapping("/operator-assignments/{id}/status")
    public ResponseEntity<?> updateAssignmentStatus(@PathVariable UUID id,
            @RequestParam AssignmentStatus status) {
        try {
            OperatorAssignmentResponseDto responseDto = operatorAssignmentService.updateAssignmentStatus(id,
                    status);
            return new ResponseEntityBuilder.Builder<Map<String, Object>>()
                    .resultMessage("Assignment status updated successfully")
                    .httpStatus(HttpStatus.OK)
                    .body(Map.of("assignment", responseDto))
                    .buildWrapped();
        } catch (RuntimeException e) {
            return new ResponseEntityBuilder.Builder<Map<String, Object>>()
                    .resultMessage("Assignment not found")
                    .httpStatus(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()))
                    .buildWrapped();
        }
    }

    /**
     * Get most recent assignment for operator.
     */
    @GetMapping("/operator-assignments/operator/{operatorId}/recent")
    public ResponseEntity<?> getMostRecentAssignmentForOperator(
            @PathVariable UUID operatorId) {
        try {
            OperatorAssignmentListResponseDto responseDto = operatorAssignmentService
                    .getMostRecentAssignmentForOperator(operatorId);
            return new ResponseEntityBuilder.Builder<Map<String, Object>>()
                    .resultMessage("Recent assignment for operator retrieved successfully")
                    .httpStatus(HttpStatus.OK)
                    .body(Map.of("assignment", responseDto))
                    .buildWrapped();
        } catch (RuntimeException e) {
            return new ResponseEntityBuilder.Builder<Map<String, Object>>()
                    .resultMessage("No recent assignment found for operator")
                    .httpStatus(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()))
                    .buildWrapped();
        }
    }

    /**
     * Get most recent assignment for bus.
     */
    @GetMapping("/operator-assignments/bus/{busId}/recent")
    public ResponseEntity<?> getMostRecentAssignmentForBus(@PathVariable UUID busId) {
        try {
            OperatorAssignmentListResponseDto responseDto = operatorAssignmentService
                    .getMostRecentAssignmentForBus(busId);
            return new ResponseEntityBuilder.Builder<Map<String, Object>>()
                    .resultMessage("Recent assignment for bus retrieved successfully")
                    .httpStatus(HttpStatus.OK)
                    .body(Map.of("assignment", responseDto))
                    .buildWrapped();
        } catch (RuntimeException e) {
            return new ResponseEntityBuilder.Builder<Map<String, Object>>()
                    .resultMessage("No recent assignment found for bus")
                    .httpStatus(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()))
                    .buildWrapped();
        }
    }

    /**
     * Get assignment statistics.
     */
    @GetMapping("/operator-assignments/statistics")
    public ResponseEntity<?> getAssignmentStatistics() {
        AssignmentStatsDto stats = operatorAssignmentService.getAssignmentStatistics();
        return new ResponseEntityBuilder.Builder<Map<String, Object>>()
                .resultMessage("Assignment statistics retrieved successfully")
                .httpStatus(HttpStatus.OK)
                .body(Map.of("statistics", stats))
                .buildWrapped();
    }
}