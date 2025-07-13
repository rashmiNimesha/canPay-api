package com.canpay.api.controller.canpayadmin;

import com.canpay.api.dto.dashboard.operatorassignment.OperatorAssignmentRequestDto;
import com.canpay.api.dto.dashboard.operatorassignment.OperatorAssignmentResponseDto;
import com.canpay.api.dto.dashboard.operatorassignment.OperatorAssignmentSearchDto;
import com.canpay.api.entity.OperatorAssignment.AssignmentStatus;
import com.canpay.api.service.dashboard.DOperatorAssignmentService;
import com.canpay.api.service.dashboard.DOperatorAssignmentService.AssignmentStatsDto;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
    public ResponseEntity<OperatorAssignmentResponseDto> createAssignment(
            @Valid @RequestBody OperatorAssignmentRequestDto requestDto) {
        try {
            OperatorAssignmentResponseDto responseDto = operatorAssignmentService.createAssignment(requestDto);
            return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get assignment by ID.
     */
    @GetMapping("/operator-assignments/{id}")
    public ResponseEntity<OperatorAssignmentResponseDto> getAssignmentById(@PathVariable UUID id) {
        try {
            OperatorAssignmentResponseDto responseDto = operatorAssignmentService.getAssignmentById(id);
            return new ResponseEntity<>(responseDto, HttpStatus.OK);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get all assignments.
     */
    @GetMapping("/operator-assignments")
    public ResponseEntity<List<OperatorAssignmentResponseDto>> getAllAssignments() {
        List<OperatorAssignmentResponseDto> assignments = operatorAssignmentService.getAllAssignments();
        return new ResponseEntity<>(assignments, HttpStatus.OK);
    }

    /**
     * Update assignment.
     */
    @PutMapping("/operator-assignments/{id}")
    public ResponseEntity<OperatorAssignmentResponseDto> updateAssignment(@PathVariable UUID id,
            @Valid @RequestBody OperatorAssignmentRequestDto requestDto) {
        try {
            OperatorAssignmentResponseDto responseDto = operatorAssignmentService.updateAssignment(id, requestDto);
            return new ResponseEntity<>(responseDto, HttpStatus.OK);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Delete assignment.
     */
    @DeleteMapping("/operator-assignments/{id}")
    public ResponseEntity<Void> deleteAssignment(@PathVariable UUID id) {
        try {
            operatorAssignmentService.deleteAssignment(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Search assignments based on criteria.
     */
    @PostMapping("/operator-assignments/search")
    public ResponseEntity<List<OperatorAssignmentResponseDto>> searchAssignments(
            @RequestBody OperatorAssignmentSearchDto searchDto) {
        List<OperatorAssignmentResponseDto> assignments = operatorAssignmentService.searchAssignments(searchDto);
        return new ResponseEntity<>(assignments, HttpStatus.OK);
    }

    /**
     * Get assignments by operator ID.
     */
    @GetMapping("/operator-assignments/operator/{operatorId}")
    public ResponseEntity<List<OperatorAssignmentResponseDto>> getAssignmentsByOperator(@PathVariable UUID operatorId) {
        List<OperatorAssignmentResponseDto> assignments = operatorAssignmentService
                .getAssignmentsByOperator(operatorId);
        return new ResponseEntity<>(assignments, HttpStatus.OK);
    }

    /**
     * Get assignments by bus ID.
     */
    @GetMapping("/operator-assignments/bus/{busId}")
    public ResponseEntity<List<OperatorAssignmentResponseDto>> getAssignmentsByBus(@PathVariable UUID busId) {
        List<OperatorAssignmentResponseDto> assignments = operatorAssignmentService.getAssignmentsByBus(busId);
        return new ResponseEntity<>(assignments, HttpStatus.OK);
    }

    /**
     * Get assignments by status.
     */
    @GetMapping("/operator-assignments/status/{status}")
    public ResponseEntity<List<OperatorAssignmentResponseDto>> getAssignmentsByStatus(
            @PathVariable AssignmentStatus status) {
        List<OperatorAssignmentResponseDto> assignments = operatorAssignmentService.getAssignmentsByStatus(status);
        return new ResponseEntity<>(assignments, HttpStatus.OK);
    }

    /**
     * Get assignments by bus owner ID.
     */
    @GetMapping("/operator-assignments/bus-owner/{ownerId}")
    public ResponseEntity<List<OperatorAssignmentResponseDto>> getAssignmentsByBusOwner(@PathVariable UUID ownerId) {
        List<OperatorAssignmentResponseDto> assignments = operatorAssignmentService.getAssignmentsByBusOwner(ownerId);
        return new ResponseEntity<>(assignments, HttpStatus.OK);
    }

    /**
     * Update assignment status.
     */
    @PatchMapping("/operator-assignments/{id}/status")
    public ResponseEntity<OperatorAssignmentResponseDto> updateAssignmentStatus(@PathVariable UUID id,
            @RequestParam AssignmentStatus status) {
        try {
            OperatorAssignmentResponseDto responseDto = operatorAssignmentService.updateAssignmentStatus(id, status);
            return new ResponseEntity<>(responseDto, HttpStatus.OK);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get most recent assignment for operator.
     */
    @GetMapping("/operator-assignments/operator/{operatorId}/recent")
    public ResponseEntity<OperatorAssignmentResponseDto> getMostRecentAssignmentForOperator(
            @PathVariable UUID operatorId) {
        try {
            OperatorAssignmentResponseDto responseDto = operatorAssignmentService
                    .getMostRecentAssignmentForOperator(operatorId);
            return new ResponseEntity<>(responseDto, HttpStatus.OK);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get most recent assignment for bus.
     */
    @GetMapping("/operator-assignments/bus/{busId}/recent")
    public ResponseEntity<OperatorAssignmentResponseDto> getMostRecentAssignmentForBus(@PathVariable UUID busId) {
        try {
            OperatorAssignmentResponseDto responseDto = operatorAssignmentService.getMostRecentAssignmentForBus(busId);
            return new ResponseEntity<>(responseDto, HttpStatus.OK);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get assignment statistics.
     */
    @GetMapping("/operator-assignments/statistics")
    public ResponseEntity<AssignmentStatsDto> getAssignmentStatistics() {
        AssignmentStatsDto stats = operatorAssignmentService.getAssignmentStatistics();
        return new ResponseEntity<>(stats, HttpStatus.OK);
    }

    /**
     * Approve assignment (set status to ACTIVE).
     */
    @PatchMapping("/operator-assignments/{id}/approve")
    public ResponseEntity<OperatorAssignmentResponseDto> approveAssignment(@PathVariable UUID id) {
        try {
            OperatorAssignmentResponseDto responseDto = operatorAssignmentService.updateAssignmentStatus(id,
                    AssignmentStatus.ACTIVE);
            return new ResponseEntity<>(responseDto, HttpStatus.OK);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Reject assignment (set status to REJECTED).
     */
    @PatchMapping("/operator-assignments/{id}/reject")
    public ResponseEntity<OperatorAssignmentResponseDto> rejectAssignment(@PathVariable UUID id) {
        try {
            OperatorAssignmentResponseDto responseDto = operatorAssignmentService.updateAssignmentStatus(id,
                    AssignmentStatus.REJECTED);
            return new ResponseEntity<>(responseDto, HttpStatus.OK);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Block assignment (set status to BLOCKED).
     */
    @PatchMapping("/operator-assignments/{id}/block")
    public ResponseEntity<OperatorAssignmentResponseDto> blockAssignment(@PathVariable UUID id) {
        try {
            OperatorAssignmentResponseDto responseDto = operatorAssignmentService.updateAssignmentStatus(id,
                    AssignmentStatus.BLOCKED);
            return new ResponseEntity<>(responseDto, HttpStatus.OK);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Activate assignment (set status to ACTIVE).
     */
    @PatchMapping("/operator-assignments/{id}/activate")
    public ResponseEntity<OperatorAssignmentResponseDto> activateAssignment(@PathVariable UUID id) {
        try {
            OperatorAssignmentResponseDto responseDto = operatorAssignmentService.updateAssignmentStatus(id,
                    AssignmentStatus.ACTIVE);
            return new ResponseEntity<>(responseDto, HttpStatus.OK);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Deactivate assignment (set status to INACTIVE).
     */
    @PatchMapping("/operator-assignments/{id}/deactivate")
    public ResponseEntity<OperatorAssignmentResponseDto> deactivateAssignment(@PathVariable UUID id) {
        try {
            OperatorAssignmentResponseDto responseDto = operatorAssignmentService.updateAssignmentStatus(id,
                    AssignmentStatus.INACTIVE);
            return new ResponseEntity<>(responseDto, HttpStatus.OK);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}