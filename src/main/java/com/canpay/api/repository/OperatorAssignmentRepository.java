//package com.canpay.api.repository;
//
//import com.canpay.api.entity.OperatorAssignment;
//import com.canpay.api.entity.User;
//import com.canpay.api.entity.Bus;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.stereotype.Repository;
//
//import java.util.Optional;
//import java.util.UUID;
//
//@Repository
//public interface OperatorAssignmentRepository extends JpaRepository<OperatorAssignment, UUID> {
//    Optional<OperatorAssignment> findByOperatorAndBus(User operator, Bus bus);
//    Optional<OperatorAssignment> findByBusIdAndOperatorIdAndStatus(UUID busId, UUID operatorId, OperatorAssignment.AssignmentStatus status);
//    OperatorAssignment findFirstByOperatorIdOrderByAssignedAtDesc(UUID operatorId);
//    Optional<OperatorAssignment> findByOperatorAndBusAndStatus(User operator, Bus bus, OperatorAssignment.AssignmentStatus status);
//}