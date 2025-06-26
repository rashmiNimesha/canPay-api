package com.canpay.api.repository.user;

import com.canpay.api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    List<User> findByRole(String role);

    @Query("SELECT COUNT(u) FROM User u WHERE u.role = 'PASSENGER'")
    long countPassengers();
}
