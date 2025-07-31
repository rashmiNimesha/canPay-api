package com.canpay.api.service.dashboard;

import com.canpay.api.dto.dashboard.operatorassignment.OperatorAssignmentRequestDto;
import com.canpay.api.dto.dashboard.operatorassignment.OperatorAssignmentListResponseDto;
import com.canpay.api.dto.dashboard.operatorassignment.OperatorAssignmentResponseDto;
import com.canpay.api.dto.dashboard.operatorassignment.OperatorAssignmentSearchDto;
import com.canpay.api.dto.dashboard.bus.BusResponseDto;
import com.canpay.api.dto.dashboard.user.UserDto;
import com.canpay.api.entity.Bus;
import com.canpay.api.entity.OperatorAssignment;
import com.canpay.api.entity.OperatorAssignment.AssignmentStatus;
import com.canpay.api.entity.User;
import com.canpay.api.repository.dashboard.DBusRepository;
import com.canpay.api.repository.dashboard.DOperatorAssignmentRepository;
import com.canpay.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service class for managing OperatorAssignment entities.
 * Provides business logic for operator assignment operations.
 */
@Service
@Transactional
public class DOperatorAssignmentService {

    @Autowired
    private DOperatorAssignmentRepository operatorAssignmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DBusRepository busRepository;

    @Autowired
    private DBusService busService;

    @Autowired
    private DUserService userService;

    /**
     * Create a new operator assignment.
     */
    public OperatorAssignmentResponseDto createAssignment(OperatorAssignmentRequestDto requestDto) {
        Optional<User> operatorOpt = userRepository.findById(requestDto.getOperatorId());
        if (!operatorOpt.isPresent()) {
            throw new RuntimeException("Operator not found with ID: " + requestDto.getOperatorId());
        }
        User operator = operatorOpt.get();

        Optional<Bus> busOpt = busRepository.findById(requestDto.getBusId());
        if (!busOpt.isPresent()) {
            throw new RuntimeException("Bus not found with ID: " + requestDto.getBusId());
        }
        Bus bus = busOpt.get();

        OperatorAssignment assignment = new OperatorAssignment(
                operator,
                bus,
                requestDto.getStatus() != null ? requestDto.getStatus() : AssignmentStatus.PENDING);

        if (requestDto.getAssignedAt() != null) {
            assignment.setAssignedAt(requestDto.getAssignedAt());
        }

        OperatorAssignment savedAssignment = operatorAssignmentRepository.save(assignment);
        return convertToSingleResponseDto(savedAssignment);
    }

    /**
     * Get assignment by ID.
     */
    @Transactional(readOnly = true)
    public OperatorAssignmentResponseDto getAssignmentById(UUID id) {
        Optional<OperatorAssignment> assignmentOpt = operatorAssignmentRepository.findById(id);
        if (!assignmentOpt.isPresent()) {
            throw new RuntimeException("Assignment not found with ID: " + id);
        }
        return convertToSingleResponseDto(assignmentOpt.get());
    }

    /**
     * Get all assignments.
     */
    @Transactional(readOnly = true)
    public List<OperatorAssignmentListResponseDto> getAllAssignments() {
        return operatorAssignmentRepository.findAll().stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Update assignment.
     */
    public OperatorAssignmentResponseDto updateAssignment(UUID id, OperatorAssignmentRequestDto requestDto) {
        Optional<OperatorAssignment> assignmentOpt = operatorAssignmentRepository.findById(id);
        if (!assignmentOpt.isPresent()) {
            throw new RuntimeException("Assignment not found with ID: " + id);
        }
        OperatorAssignment existingAssignment = assignmentOpt.get();

        if (requestDto.getOperatorId() != null
                && !requestDto.getOperatorId().equals(existingAssignment.getUser().getId())) {
            Optional<User> operatorOpt = userRepository.findById(requestDto.getOperatorId());
            if (!operatorOpt.isPresent()) {
                throw new RuntimeException("Operator not found with ID: " + requestDto.getOperatorId());
            }
            existingAssignment.setUser(operatorOpt.get());
        }

        if (requestDto.getBusId() != null && !requestDto.getBusId().equals(existingAssignment.getBus().getId())) {
            Optional<Bus> busOpt = busRepository.findById(requestDto.getBusId());
            if (!busOpt.isPresent()) {
                throw new RuntimeException("Bus not found with ID: " + requestDto.getBusId());
            }
            existingAssignment.setBus(busOpt.get());
        }

        if (requestDto.getStatus() != null) {
            existingAssignment.setStatus(requestDto.getStatus());
        }

        if (requestDto.getAssignedAt() != null) {
            existingAssignment.setAssignedAt(requestDto.getAssignedAt());
        }

        OperatorAssignment updatedAssignment = operatorAssignmentRepository.save(existingAssignment);
        return convertToSingleResponseDto(updatedAssignment);
    }

    /**
     * Delete assignment by ID.
     */
    public void deleteAssignment(UUID id) {
        if (!operatorAssignmentRepository.existsById(id)) {
            throw new RuntimeException("Assignment not found with ID: " + id);
        }
        operatorAssignmentRepository.deleteById(id);
    }

    /**
     * Search assignments based on criteria.
     */
    @Transactional(readOnly = true)
    public List<OperatorAssignmentListResponseDto> searchAssignments(OperatorAssignmentSearchDto searchDto) {
        List<OperatorAssignment> assignments;

        if (searchDto.getOperatorId() != null && searchDto.getStatus() != null) {
            assignments = operatorAssignmentRepository.findByOperator_IdAndStatus(searchDto.getOperatorId(),
                    searchDto.getStatus());
        } else if (searchDto.getBusId() != null && searchDto.getStatus() != null) {
            assignments = operatorAssignmentRepository.findByBus_IdAndStatus(searchDto.getBusId(),
                    searchDto.getStatus());
        } else if (searchDto.getOperatorId() != null && searchDto.getBusId() != null) {
            assignments = operatorAssignmentRepository.findByOperator_IdAndBus_Id(searchDto.getOperatorId(),
                    searchDto.getBusId());
        } else if (searchDto.getOperatorId() != null && searchDto.getStatuses() != null
                && !searchDto.getStatuses().isEmpty()) {
            assignments = operatorAssignmentRepository.findByOperator_IdAndStatusIn(searchDto.getOperatorId(),
                    searchDto.getStatuses());
        } else if (searchDto.getBusId() != null && searchDto.getStatuses() != null
                && !searchDto.getStatuses().isEmpty()) {
            assignments = operatorAssignmentRepository.findByBus_IdAndStatusIn(searchDto.getBusId(),
                    searchDto.getStatuses());
        } else if (searchDto.getOperatorId() != null) {
            assignments = operatorAssignmentRepository.findByOperator_Id(searchDto.getOperatorId());
        } else if (searchDto.getBusId() != null) {
            assignments = operatorAssignmentRepository.findByBus_Id(searchDto.getBusId());
        } else if (searchDto.getBusOwnerId() != null) {
            assignments = operatorAssignmentRepository.findByBus_Owner_Id(searchDto.getBusOwnerId());
        } else if (searchDto.getStatus() != null) {
            assignments = operatorAssignmentRepository.findByStatus(searchDto.getStatus());
        } else if (searchDto.getStatuses() != null && !searchDto.getStatuses().isEmpty()) {
            assignments = operatorAssignmentRepository.findByStatusIn(searchDto.getStatuses());
        } else if (searchDto.getAssignedAfter() != null && searchDto.getAssignedBefore() != null) {
            assignments = operatorAssignmentRepository.findByAssignedAtBetween(searchDto.getAssignedAfter(),
                    searchDto.getAssignedBefore());
        } else if (searchDto.getCreatedAfter() != null && searchDto.getCreatedBefore() != null) {
            assignments = operatorAssignmentRepository.findByCreatedAtBetween(searchDto.getCreatedAfter(),
                    searchDto.getCreatedBefore());
        } else if (searchDto.getUpdatedAfter() != null && searchDto.getUpdatedBefore() != null) {
            assignments = operatorAssignmentRepository.findByUpdatedAtBetween(searchDto.getUpdatedAfter(),
                    searchDto.getUpdatedBefore());
        } else {
            assignments = operatorAssignmentRepository.findAll();
        }

        return assignments.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Get assignments by operator ID.
     */
    @Transactional(readOnly = true)
    public List<OperatorAssignmentListResponseDto> getAssignmentsByOperator(UUID operatorId) {
        return operatorAssignmentRepository.findByOperator_Id(operatorId).stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Get assignments by bus ID.
     */
    @Transactional(readOnly = true)
    public List<OperatorAssignmentListResponseDto> getAssignmentsByBus(UUID busId) {
        return operatorAssignmentRepository.findByBus_Id(busId).stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Get assignments by status.
     */
    @Transactional(readOnly = true)
    public List<OperatorAssignmentListResponseDto> getAssignmentsByStatus(AssignmentStatus status) {
        return operatorAssignmentRepository.findByStatus(status).stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Get assignments by bus owner ID.
     */
    @Transactional(readOnly = true)
    public List<OperatorAssignmentListResponseDto> getAssignmentsByBusOwner(UUID ownerId) {
        return operatorAssignmentRepository.findByBus_Owner_Id(ownerId).stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Update assignment status.
     */
    public OperatorAssignmentResponseDto updateAssignmentStatus(UUID id, AssignmentStatus status) {
        Optional<OperatorAssignment> assignmentOpt = operatorAssignmentRepository.findById(id);
        if (!assignmentOpt.isPresent()) {
            throw new RuntimeException("Assignment not found with ID: " + id);
        }
        OperatorAssignment assignment = assignmentOpt.get();

        assignment.setStatus(status);
        OperatorAssignment updatedAssignment = operatorAssignmentRepository.save(assignment);
        return convertToSingleResponseDto(updatedAssignment);
    }

    /**
     * Get most recent assignment for operator.
     */
    @Transactional(readOnly = true)
    public OperatorAssignmentListResponseDto getMostRecentAssignmentForOperator(UUID operatorId) {
        Optional<OperatorAssignment> assignmentOpt = operatorAssignmentRepository
                .findTopByOperator_IdOrderByAssignedAtDesc(operatorId);
        if (!assignmentOpt.isPresent()) {
            throw new RuntimeException("No assignment found for operator ID: " + operatorId);
        }
        return convertToResponseDto(assignmentOpt.get());
    }

    /**
     * Get most recent assignment for bus.
     */
    @Transactional(readOnly = true)
    public OperatorAssignmentListResponseDto getMostRecentAssignmentForBus(UUID busId) {
        Optional<OperatorAssignment> assignmentOpt = operatorAssignmentRepository
                .findTopByBus_IdOrderByAssignedAtDesc(busId);
        if (!assignmentOpt.isPresent()) {
            throw new RuntimeException("No assignment found for bus ID: " + busId);
        }
        return convertToResponseDto(assignmentOpt.get());
    }

    /**
     * Get assignment statistics.
     */
    @Transactional(readOnly = true)
    public AssignmentStatsDto getAssignmentStatistics() {
        long totalAssignments = operatorAssignmentRepository.count();
        long activeAssignments = operatorAssignmentRepository.countByStatus(AssignmentStatus.ACTIVE);
        long pendingAssignments = operatorAssignmentRepository.countByStatus(AssignmentStatus.PENDING);
        long inactiveAssignments = operatorAssignmentRepository.countByStatus(AssignmentStatus.INACTIVE);
        long rejectedAssignments = operatorAssignmentRepository.countByStatus(AssignmentStatus.REJECTED);
        long blockedAssignments = operatorAssignmentRepository.countByStatus(AssignmentStatus.BLOCKED);

        return new AssignmentStatsDto(totalAssignments, activeAssignments, pendingAssignments,
                inactiveAssignments, rejectedAssignments, blockedAssignments);
    }

    /**
     * Convert OperatorAssignment entity to OperatorAssignmentResponseDto.
     */
    private OperatorAssignmentResponseDto convertToSingleResponseDto(OperatorAssignment assignment) {
        OperatorAssignmentResponseDto dto = new OperatorAssignmentResponseDto();
        dto.setId(assignment.getId());

        // Create full operator DTO
        UserDto operatorDto = new UserDto(assignment.getUser());
        // Set photo as public URL if exists
        String publicPhotoUrl = userService.getPublicPhotoUrl(assignment.getUser().getPhotoUrl());
        if (publicPhotoUrl != null) {
            operatorDto.setPhoto(publicPhotoUrl);
        }
        dto.setOperator(operatorDto);

        // Create full bus DTO
        BusResponseDto busDto = busService.getBusById(assignment.getBus().getId());
        dto.setBus(busDto);

        dto.setStatus(assignment.getStatus());
        dto.setAssignedAt(assignment.getAssignedAt());
        dto.setCreatedAt(assignment.getCreatedAt());
        dto.setUpdatedAt(assignment.getUpdatedAt());
        return dto;
    }

    /**
     * Convert OperatorAssignment entity to OperatorAssignmentListResponseDto.
     */
    private OperatorAssignmentListResponseDto convertToResponseDto(OperatorAssignment assignment) {
        OperatorAssignmentListResponseDto dto = new OperatorAssignmentListResponseDto();
        dto.setId(assignment.getId());
        dto.setOperatorId(assignment.getUser().getId());
        dto.setOperatorName(assignment.getUser().getName());
        dto.setBusId(assignment.getBus().getId());
        dto.setBusNumber(assignment.getBus().getBusNumber());
        dto.setBusOwnerId(assignment.getBus().getOwner().getId());
        dto.setBusOwnerName(assignment.getBus().getOwner().getName());
        dto.setStatus(assignment.getStatus());
        dto.setAssignedAt(assignment.getAssignedAt());
        dto.setCreatedAt(assignment.getCreatedAt());
        dto.setUpdatedAt(assignment.getUpdatedAt());
        return dto;
    }

    /**
     * Inner class for assignment statistics.
     */
    public static class AssignmentStatsDto {
        private long totalAssignments;
        private long activeAssignments;
        private long pendingAssignments;
        private long inactiveAssignments;
        private long rejectedAssignments;
        private long blockedAssignments;

        public AssignmentStatsDto(long totalAssignments, long activeAssignments, long pendingAssignments,
                long inactiveAssignments, long rejectedAssignments, long blockedAssignments) {
            this.totalAssignments = totalAssignments;
            this.activeAssignments = activeAssignments;
            this.pendingAssignments = pendingAssignments;
            this.inactiveAssignments = inactiveAssignments;
            this.rejectedAssignments = rejectedAssignments;
            this.blockedAssignments = blockedAssignments;
        }

        // Getters
        public long getTotalAssignments() {
            return totalAssignments;
        }

        public long getActiveAssignments() {
            return activeAssignments;
        }

        public long getPendingAssignments() {
            return pendingAssignments;
        }

        public long getInactiveAssignments() {
            return inactiveAssignments;
        }

        public long getRejectedAssignments() {
            return rejectedAssignments;
        }

        public long getBlockedAssignments() {
            return blockedAssignments;
        }
    }

    public long getTotalOperatorsAssignedToOwner(UUID ownerId) {
        return operatorAssignmentRepository.countDistinctOperatorsByOwnerId(ownerId);
    }
}