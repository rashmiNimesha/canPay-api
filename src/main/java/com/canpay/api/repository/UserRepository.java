package com.canpay.api.repository;

import com.canpay.api.entity.User;
import com.canpay.api.entity.User.UserRole;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    List<User> findByRole(UserRole role);
    Optional<User> findByNic(String nic);
    Optional<User> findById(UUID id);

    @Query("SELECT COUNT(u) FROM User u WHERE u.role = com.canpay.api.entity.User$UserRole.PASSENGER")
    long countPassengers();
    boolean existsByEmail(String email);

}
