package com.canpay.api.repository.dashboard;

import com.canpay.api.entity.User;
import com.canpay.api.entity.User.UserRole;
import com.canpay.api.entity.User.UserStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.time.LocalDateTime;

@Repository
public interface DUserRepository extends JpaRepository<User, Long> {
    // Find user by ID
    Optional<User> findById(UUID id);

    // Find user by email
    Optional<User> findByEmail(String email);

    // Find users by name
    List<User> findByName(String name);

    // Find user by NIC
    Optional<User> findByNic(String nic);

    // Find users by role
    List<User> findByRole(UserRole role);

    // Find users by status
    List<User> findByStatus(UserStatus status);

    // Find users created within a date range
    List<User> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    // Find users updated within a date range
    List<User> findByUpdatedAtBetween(LocalDateTime start, LocalDateTime end);

    // Find user by bank account ID
    Optional<User> findByBankAccounts_Id(UUID bankAccountId);

    // Find user by owned bus ID
    Optional<User> findByOwnedBuses_Id(UUID busId);

    // Find user by operator assignment ID
    Optional<User> findByOperatorAssignments_Id(UUID assignmentId);

    // Find user by wallet ID
    Optional<User> findByWallet_Id(UUID walletId);

    // Count users by status
    long countByStatus(UserStatus status);

    // Count users by role
    long countByRole(UserRole role);

    // Find users by name containing a substring
    List<User> findByNameContaining(String name);

    // Find users by email containing a substring
    List<User> findByEmailContaining(String email);

    // Find users by multiple roles
    List<User> findByRoleIn(List<UserRole> roles);

    // Find users by multiple statuses
    List<User> findByStatusIn(List<UserStatus> statuses);

    // Find users by role and status
    List<User> findByRoleAndStatus(UserRole role, UserStatus status);

    // Find user by NIC and role to check for role-specific uniqueness
    Optional<User> findByNicAndRole(String nic, UserRole role);

    // Find user by Email and role to check for role-specific uniqueness
    Optional<User> findByEmailAndRole(String email, UserRole role);

    // Delete user by ID
    void deleteById(UUID id);
}
