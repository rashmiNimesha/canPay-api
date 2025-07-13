package com.canpay.api.repository;

import com.canpay.api.entity.OperatorAssignment;
import com.canpay.api.entity.User;
import com.canpay.api.entity.Bus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface OperatorAssignmentRepository extends JpaRepository<OperatorAssignment, UUID> {
    Optional<OperatorAssignment> findByOperatorAndBus(User operator, Bus bus);
}