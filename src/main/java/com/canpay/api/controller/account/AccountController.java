package com.canpay.api.controller.account;


import com.canpay.api.dto.dashboard.bus.BusResponseDto;
import com.canpay.api.dto.dashboard.operatorassignment.OperatorAssignmentRequestDto;
import com.canpay.api.dto.dashboard.operatorassignment.OperatorAssignmentResponseDto;
import com.canpay.api.dto.dashboard.user.UserDto;
import com.canpay.api.entity.Bus;
import com.canpay.api.entity.OperatorAssignment;
import com.canpay.api.entity.ResponseEntityBuilder;
import com.canpay.api.entity.User;
import com.canpay.api.service.dashboard.DBusService;
import com.canpay.api.service.dashboard.DOperatorAssignmentService;
import com.canpay.api.service.implementation.BankAccountServiceImpl;
import com.canpay.api.service.implementation.JwtService;
import com.canpay.api.service.implementation.TransactionService;
import com.canpay.api.service.implementation.UserServiceImpl;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/user-service")
public class AccountController {

    public final UserServiceImpl userService;
    private final JwtService jwtService;
    private final BankAccountServiceImpl bankAccountService;
    private final Logger logger = LoggerFactory.getLogger(AccountController.class);
    private final DOperatorAssignmentService operatorAssignmentService;
    private final DBusService busService;
    private final TransactionService transactionService;

    public AccountController(UserServiceImpl userService, JwtService jwtService,
                             BankAccountServiceImpl bankAccountService, DOperatorAssignmentService operatorAssignmentService, DBusService busService, TransactionService transactionService) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.bankAccountService = bankAccountService;
        this.operatorAssignmentService = operatorAssignmentService;
        this.busService = busService;
        this.transactionService = transactionService;
    }


    @PatchMapping("/passenger-account")
    @PreAuthorize("hasRole('PASSENGER')")
    public ResponseEntity<?> updatePassengerAccount(
            @RequestHeader(value = "Authorization") String authHeader,
            @RequestBody Map<String, String> request) {
        logger.debug("Received passenger account update request: {}", request);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.warn("Authorization header missing or invalid");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "Authorization header with Bearer token is required"));
        }

        String token = authHeader.substring(7);
        String email;

        try {
            email = jwtService.extractEmail(token);
            String tokenRole = jwtService.extractRole(token);
            if (!"PASSENGER".equals(tokenRole)) {
                logger.warn("Invalid role in token: {}", tokenRole);
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("success", false, "message", "Invalid role for passenger account update"));
            }
        } catch (Exception e) {
            logger.warn("Invalid token: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "Invalid or expired token"));
        }

        try {
            User user = userService.findUserByEmail(email)
                    .orElseThrow(() -> {
                        logger.warn("User not found for email: {}", email);
                        return new RuntimeException("User not found for email: " + email);
                    });

            if (!"PASSENGER".equals(user.getRole().toString())) {
                logger.warn("User is not a passenger: {}", email);
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("success", false, "message", "User is not a passenger"));
            }

            boolean updated = false;
            String newToken = "";
            if (request.containsKey("name")) {
                String name = request.get("name");
                user = userService.updateName(email, name);
                updated = true;
            }

            String newEmail = request.get("newemail");
            if (newEmail != null && !newEmail.equalsIgnoreCase(email)) {
                // Check if new email already exists
                Optional<User> existingUser = userService.findUserByEmail(newEmail);
                if (existingUser.isPresent()) {
                    logger.warn("New email already exists in database: {}", newEmail);
                    return ResponseEntity.badRequest()
                            .body(Map.of("success", false, "message", "New email is already registered"));
                }
                user = userService.updateEmail(email, newEmail);
                newToken = jwtService.generateToken(user); // Generate new token for email update
                updated = true;
            }

            if (!updated) {
                logger.warn("No updates provided in request for email: {}", email);
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "message", "No valid update fields provided"));
            }

            UserDto userDto = new UserDto(user.getName(), user.getEmail());
            Map<String, Object> data = Map.of(
                    "profile", userDto,
                    "token", newToken
            );

            logger.info("Passenger account updated for email: {}, newEmail: {}, nameUpdated: {}",
                    email, newEmail != null ? newEmail : "none", request.containsKey("name"));

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Passenger account updated",
                    "data", data
            ));

        } catch (IllegalArgumentException e) {
            logger.warn("Invalid input for email: {}. Reason: {}", email, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", e.getMessage()));
        } catch (RuntimeException e) {
            logger.error("Failed to update passenger account for email: {}. Reason: {}", email, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Failed to update passenger account: " + e.getMessage()));
        }
    }

    @GetMapping("/financial-details")
    @PreAuthorize("hasAnyRole('PASSENGER', 'OWNER')")
    public ResponseEntity<?> getUserFinancialDetails(@RequestHeader(value = "Authorization") String authHeader) {
        logger.debug("Received request for user financial details");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.warn("Authorization header missing or invalid");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "Authorization header with Bearer token is required"));
        }

        String token = authHeader.substring(7);
        String email;
        User.UserRole userRole;
        try {
            email = jwtService.extractEmail(token);
            userRole = User.UserRole.valueOf(jwtService.extractRole(token));
        } catch (Exception e) {
            logger.warn("Invalid token: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "Invalid or expired token"));
        }

        try {
            Map<String, Object> financialDetails = userService.getUserFinancialDetails(email, userRole);
            logger.info("Returning financial details for user: {}", email);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", financialDetails
            ));
        } catch (RuntimeException e) {
            logger.error("Error fetching financial details: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false, "message", e.getMessage()));
        } catch (Exception e) {
            logger.error("Unexpected error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Error fetching financial details"));
        }
    }

    /**
     * Assigns an operator to a bus.
     *
     * @param authHeader Authorization header containing the Bearer token.
     * @param requestDto Request DTO containing bus ID, operator ID, and status.
     * @return ResponseEntity with the assignment result.
     */
    @PostMapping("/operator-assignments")
    @PreAuthorize("hasRole('OWNER')")
    @Transactional
    public ResponseEntity<?> assignOperator(
            @RequestHeader(value = "Authorization") String authHeader,
            @Valid @RequestBody OperatorAssignmentRequestDto requestDto) {
        logger.debug("Received operator assignment request: busId={}, operatorId={}, status={}",
                requestDto.getBusId(), requestDto.getOperatorId(), requestDto.getStatus());

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.warn("Authorization header missing or invalid");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "Authorization header with Bearer token is required"));
        }

        String token = authHeader.substring(7);
        String ownerEmail;
        try {
            ownerEmail = jwtService.extractEmail(token);
            String tokenRole = jwtService.extractRole(token);
            if (!"OWNER".equals(tokenRole)) {
                logger.warn("Invalid role in token: {}", tokenRole);
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("success", false, "message", "Only owners can assign operators"));
            }
        } catch (Exception e) {
            logger.warn("Invalid token: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "Invalid or expired token"));
        }

        try {
            // Validate owner
            User owner = userService.findUserByEmailAndRole(ownerEmail, User.UserRole.OWNER)
                    .orElseThrow(() -> {
                        logger.warn("Owner not found for email: {}", ownerEmail);
                        return new RuntimeException("Owner not found");
                    });
            logger.debug("Owner validated: id={}, email={}", owner.getId(), owner.getEmail());

            // Validate operator
            User operator = userService.findUserById(requestDto.getOperatorId())
                    .orElseThrow(() -> {
                        logger.warn("Operator not found for ID: {}", requestDto.getOperatorId());
                        return new RuntimeException("Operator not found");
                    });
            if (!User.UserRole.OPERATOR.equals(operator.getRole())) {
                logger.warn("User is not an operator: {}", requestDto.getOperatorId());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("success", false, "message", "User is not an operator"));
            }
            logger.debug("Operator validated: id={}, role={}", operator.getId(), operator.getRole());

            // Validate bus
            Bus bus = busService.findBusById(requestDto.getBusId());
            if (bus == null) {
                logger.warn("Bus not found for ID: {}", requestDto.getBusId());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("success", false, "message", "Bus not found"));
            }
            if (bus.getOwner() == null || !bus.getOwner().getId().equals(owner.getId())) {
                logger.warn("Bus {} does not belong to owner {}", requestDto.getBusId(), owner.getId());
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("success", false, "message", "Bus does not belong to this owner"));
            }
            logger.debug("Bus validated: id={}, ownerId={}", bus.getId(), bus.getOwner().getId());

            // Check for existing active assignment
            if (busService.hasActiveOperatorAssignment(requestDto.getBusId(), requestDto.getOperatorId())) {
                logger.warn("Operator {} already assigned to bus {}", requestDto.getOperatorId(), requestDto.getBusId());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("success", false, "message", "Operator already assigned to this bus"));
            }

            OperatorAssignment assignment = new OperatorAssignment(operator, bus,
                    requestDto.getStatus() != null ? requestDto.getStatus() : OperatorAssignment.AssignmentStatus.PENDING);
            logger.debug("OperatorAssignment state: id={}, busId={}, operatorId={}, status={}",
                    assignment.getId(), assignment.getBus().getId(), assignment.getOperator().getId(), assignment.getStatus());

            busService.assignOperator(assignment);

            // Create response DTO
            OperatorAssignmentResponseDto responseDto = new OperatorAssignmentResponseDto();
            responseDto.setId(assignment.getId());
            responseDto.setStatus(assignment.getStatus());
            responseDto.setAssignedAt(assignment.getAssignedAt());
            responseDto.setCreatedAt(assignment.getCreatedAt());
            responseDto.setUpdatedAt(assignment.getUpdatedAt());

            // Populate UserDto
            UserDto userDto = new UserDto();
            userDto.setId(operator.getId());
            userDto.setName(operator.getName());
            userDto.setEmail(operator.getEmail());
            responseDto.setOperator(userDto);

            // Populate BusResponseDto
            BusResponseDto busDto = new BusResponseDto();
            busDto.setId(bus.getId());
            busDto.setBusNumber(bus.getBusNumber());
            busDto.setType(bus.getType());
            busDto.setRouteFrom(bus.getRouteFrom());
            busDto.setRouteTo(bus.getRouteTo());
            busDto.setStatus(bus.getStatus());
            busDto.setOwnerId(bus.getOwner() != null ? bus.getOwner().getId() : null);
            busDto.setOwnerName(bus.getOwner() != null ? bus.getOwner().getName() : null);

            responseDto.setBus(busDto);

            logger.info("Operator {} assigned to bus {} with status {}",
                    requestDto.getOperatorId(), requestDto.getBusId(), requestDto.getStatus());

            return new ResponseEntityBuilder.Builder<Map<String, Object>>()
                    .resultMessage("Operator assigned successfully")
                    .httpStatus(HttpStatus.CREATED)
                    .body(Map.of("data", responseDto))
                    .buildWrapped();
        } catch (DataIntegrityViolationException e) {
            logger.error("Constraint violation while assigning operator: busId={}, operatorId={}, error={}",
                    requestDto.getBusId(), requestDto.getOperatorId(), e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "message", "Database constraint violation: " + e.getMessage()));
        } catch (Exception e) { // Changed from RuntimeException to Exception to catch all potential issues
            logger.error("Failed to assign operator: busId={}, operatorId={}, error={}",
                    requestDto.getBusId(), requestDto.getOperatorId(), e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "message", "Runtime exception: " + e.getMessage()));
        }
    }


    @GetMapping("/operator/financial-details")
    @PreAuthorize("hasRole('OPERATOR')")
    public ResponseEntity<?> getOperatorFinancialDetails(
            @RequestHeader(value = "Authorization") String authHeader,
            @RequestParam("operatorId") String operatorIdStr,
            @RequestParam("busId") String busIdStr) {
        logger.debug("Received request for operator financial details for operatorId={}, busId={}", operatorIdStr, busIdStr);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.warn("Authorization header missing or invalid");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "Authorization header with Bearer token is required"));
        }

        String token = authHeader.substring(7);
        try {
            String tokenRole = jwtService.extractRole(token);
            if (!"OPERATOR".equals(tokenRole)) {
                logger.warn("Invalid role in token: {}", tokenRole);
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("success", false, "message", "Only operators can access this endpoint"));
            }
        } catch (Exception e) {
            logger.warn("Invalid token: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "Invalid or expired token"));
        }

        UUID operatorId;
        UUID busId;
        try {
            operatorId = UUID.fromString(operatorIdStr);
            busId = UUID.fromString(busIdStr);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid UUID format for operatorId or busId: {}, {}", operatorIdStr, busIdStr);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "message", "Invalid UUID format for operatorId or busId"));
        }

        try {
            // Find operator
            Optional<User> operatorOpt = userService.findUserById(operatorId);
            if (operatorOpt.isEmpty() || !User.UserRole.OPERATOR.equals(operatorOpt.get().getRole())) {
                logger.warn("Operator not found or invalid role for id: {}", operatorId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("success", false, "message", "Operator not found"));
            }
            User operator = operatorOpt.get();

            // Find bus
            Bus bus = busService.findBusById(busId);
            if (bus == null) {
                logger.warn("Bus not found for id: {}", busId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("success", false, "message", "Bus not found"));
            }

            // Check for ACTIVE assignment
            boolean hasActiveAssignment = operator.getOperatorAssignments().stream()
                    .anyMatch(a -> a.getBus().getId().equals(busId)
                            && a.getStatus() == OperatorAssignment.AssignmentStatus.ACTIVE);

            if (!hasActiveAssignment) {
                logger.warn("No ACTIVE assignment found for operator {} and bus {}", operatorId, busId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("success", false, "message", "No ACTIVE assignment found for this operator and bus"));
            }

            // Get earnings for this bus
            BigDecimal earnings = transactionService.sumPaymentsForBus(busId);

            Map<String, Object> data = Map.of(
                    "operatorName", operator.getName(),
                    "busNumber", bus.getBusNumber(),
                    "earningsAmount", earnings != null ? earnings : BigDecimal.ZERO
            );

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", data
            ));
        } catch (Exception e) {
            logger.error("Error fetching operator financial details: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Error fetching operator financial details"));
        }
    }

    @GetMapping("/{ownerId}/total-operators")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<?> getTotalOperatorsAssignedToOwner(@PathVariable UUID ownerId) {
        long totalOperators = operatorAssignmentService.getTotalOperatorsAssignedToOwner(ownerId);
        return new ResponseEntityBuilder.Builder<Long>()
                .resultMessage("Total operators assigned to owner")
                .body(totalOperators)
                .buildWrapped();
    }


}

