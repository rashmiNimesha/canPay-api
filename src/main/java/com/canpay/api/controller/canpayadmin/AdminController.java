package com.canpay.api.controller.canpayadmin;


import com.canpay.api.dto.UserDto;
import com.canpay.api.entity.User;
import com.canpay.api.repository.user.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/v1/canpay-admin")
public class AdminController {

    private final UserRepository userRepository;

    public AdminController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/passengers")
    public ResponseEntity<?> getAllPassengers() {
        List<User> passengers = userRepository.findByRole("PASSENGER");

        List<UserDto> passengerDtos = passengers.stream()
                .map(UserDto::new)
                .toList();

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "message", "List of all passengers",
                        "data", passengerDtos
                )
        );
    }


    @GetMapping("/passenger-count")
    public ResponseEntity<?> getPassengerCount() {
        long count = userRepository.countPassengers();

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "message", "Total number of passengers",
                        "data", Map.of("passengerCount", count)
                )
        );
    }

}
