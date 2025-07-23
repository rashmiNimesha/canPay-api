package com.canpay.api.controller.account;

import com.canpay.api.dto.dashboard.bus.BusRequestDto;
import com.canpay.api.dto.dashboard.bus.BusResponseDto;
import com.canpay.api.entity.Bus;
import com.canpay.api.entity.User;
import com.canpay.api.repository.BusRepository;
import com.canpay.api.repository.UserRepository;
import com.canpay.api.service.dashboard.DBusService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/bus")
public class BusControllerAc {
    private final BusRepository busRepository;
    private final UserRepository userRepository;
    private final Logger logger = LoggerFactory.getLogger(BusControllerAc.class);
    @Autowired
    private   DBusService busService;

    public BusControllerAc(BusRepository busRepository, UserRepository userRepository) {
        this.busRepository = busRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/{busId}/operator/{operatorId}")
    @PreAuthorize("hasRole('PASSENGER')")
    public ResponseEntity<?> getBusAndOperatorDetails(@PathVariable String busId, @PathVariable String operatorId) {
        try {
            logger.info("Fetching bus and operator details for busId: {}, operatorId: {}", busId, operatorId);
            System.out.println("Fetching bus and operator details for busId: " + busId + ", operatorId: " + operatorId);
            UUID busUuid = UUID.fromString(busId);
            UUID operatorUuid = UUID.fromString(operatorId);

            Bus bus = busRepository.findById(busUuid)
                    .orElseThrow(() -> {
                        logger.warn("Bus not found: {}", busId);
                        return new RuntimeException("Bus not found");
                    });

            User operator = userRepository.findById(operatorUuid)
                    .orElseThrow(() -> {
                        logger.warn("Operator not found: {}", operatorId);
                        return new RuntimeException("Operator not found");
                    });

            if (!operator.getRole().equals(User.UserRole.OPERATOR)) {
                logger.warn("User is not an operator: {}", operatorId);
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "message", "Invalid operator"));
            }

            logger.info("Fetched details for bus: {}, operator: {}", busId, operatorId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", Map.of(
                            "busNumber", bus.getBusNumber(),
                            "busRoute", bus.getRouteFrom() + " - " + bus.getRouteTo(),
                            "operatorName", operator.getName()
                    )
            ));
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid UUID format: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "Invalid busId or operatorId"));
        } catch (RuntimeException e) {
            logger.error("Error fetching details: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @PostMapping("/register-buses")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<BusResponseDto> createBus(@Valid @RequestBody BusRequestDto requestDto) {
        System.out.println("come to the reg buses");
        try {
            BusResponseDto responseDto = busService.createBus(requestDto);
            return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
        }
        catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}