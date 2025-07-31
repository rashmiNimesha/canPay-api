package com.canpay.api.repository.dashboard;

import com.canpay.api.entity.OperatorAssignment;
import com.canpay.api.entity.OperatorAssignment.AssignmentStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.time.LocalDateTime;

/**
 * Dashboard repository for managing OperatorAssignment entities.
 * Provides query methods for retrieving and counting operator assignments.
 */
@Repository
public interface DOperatorAssignmentRepository extends JpaRepository<OperatorAssignment, UUID> {
    /** Find operator assignment by its UUID. */
    @Override
    @NonNull
    Optional<OperatorAssignment> findById(@NonNull UUID id);

    /** Find operator assignments by operator ID */
    List<OperatorAssignment> findByOperator_Id(UUID operatorId);

    /** Find operator assignments by bus ID */
    List<OperatorAssignment> findByBus_Id(UUID busId);

    /** Find operator assignments by status */
    List<OperatorAssignment> findByStatus(AssignmentStatus status);

    /** Find operator assignments by operator ID and status */
    List<OperatorAssignment> findByOperator_IdAndStatus(UUID operatorId, AssignmentStatus status);

    /** Find operator assignments by bus ID and status */
    List<OperatorAssignment> findByBus_IdAndStatus(UUID busId, AssignmentStatus status);

    /** Find operator assignments by operator ID and bus ID */
    List<OperatorAssignment> findByOperator_IdAndBus_Id(UUID operatorId, UUID busId);

    /** Find operator assignments created within a date range */
    List<OperatorAssignment> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    /** Find operator assignments updated within a date range */
    List<OperatorAssignment> findByUpdatedAtBetween(LocalDateTime start, LocalDateTime end);

    /** Find operator assignments assigned within a date range */
    List<OperatorAssignment> findByAssignedAtBetween(LocalDateTime start, LocalDateTime end);

    /** Count operator assignments by status */
    long countByStatus(AssignmentStatus status);

    /** Count operator assignments by operator ID */
    long countByOperator_Id(UUID operatorId);

    /** Count operator assignments by bus ID */
    long countByBus_Id(UUID busId);

    /** Count operator assignments by operator ID and status */
    long countByOperator_IdAndStatus(UUID operatorId, AssignmentStatus status);

    /** Count operator assignments by bus ID and status */
    long countByBus_IdAndStatus(UUID busId, AssignmentStatus status);

    /** Count operator assignments by status and creation date range */
    long countByStatusAndCreatedAtBetween(AssignmentStatus status, LocalDateTime start, LocalDateTime end);

    /** Find operator assignments by multiple statuses */
    List<OperatorAssignment> findByStatusIn(List<AssignmentStatus> statuses);

    /** Find operator assignments by operator ID and multiple statuses */
    List<OperatorAssignment> findByOperator_IdAndStatusIn(UUID operatorId, List<AssignmentStatus> statuses);

    /** Find operator assignments by bus ID and multiple statuses */
    List<OperatorAssignment> findByBus_IdAndStatusIn(UUID busId, List<AssignmentStatus> statuses);

    /** Find the most recent assignment for an operator */
    Optional<OperatorAssignment> findTopByOperator_IdOrderByAssignedAtDesc(UUID operatorId);

    /** Find the most recent assignment for a bus */
    Optional<OperatorAssignment> findTopByBus_IdOrderByAssignedAtDesc(UUID busId);

    /** Find operator assignments by bus owner ID */
    List<OperatorAssignment> findByBus_Owner_Id(UUID ownerId);

    /** Count operator assignments by bus owner ID */
    long countByBus_Owner_Id(UUID ownerId);

    /** Count operator assignments by bus owner ID and status */
    long countByBus_Owner_IdAndStatus(UUID ownerId, AssignmentStatus status);

    /** Delete operator assignments by operator ID */
    void deleteByOperator_Id(UUID operatorId);

    /** Delete operator assignments by bus ID */
    void deleteByBus_Id(UUID busId);

    /** Delete operator assignments by operator ID and bus ID */
    void deleteByOperator_IdAndBus_Id(UUID operatorId, UUID busId);

    @Query("SELECT COUNT(DISTINCT oa.operator.id) FROM OperatorAssignment oa WHERE oa.bus.owner.id = :ownerId")
    long countDistinctOperatorsByOwnerId(@Param("ownerId") UUID ownerId);
}