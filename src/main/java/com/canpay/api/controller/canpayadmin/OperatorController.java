package com.canpay.api.controller.canpayadmin;

import com.canpay.api.dto.dashboard.user.UserDto;
import com.canpay.api.dto.dashboard.user.UserListDto;
import com.canpay.api.dto.dashboard.user.UserRegistrationRequestDto;
import com.canpay.api.entity.ResponseEntityBuilder;
import com.canpay.api.entity.User;
import com.canpay.api.entity.User.UserRole;
import com.canpay.api.service.dashboard.DUserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import java.util.UUID;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * REST controller for managing operators in the CanPay admin dashboard.
 */
@RestController
@RequestMapping("/api/v1/canpay-admin")
public class OperatorController {
    private static final Logger logger = LoggerFactory.getLogger(OperatorController.class);

    private final DUserService userService;
    // Removed walletService and bankAccountService as operator isn't associated
    // with them

    /**
     * Constructor for OperatorController.
     * 
     * @param userService the service handling user operations
     */
    @Autowired
    public OperatorController(DUserService userService) {
        this.userService = userService;
    }

    /**
     * Adds a new operator.
     * 
     * @param request the operator registration request data
     * @return response entity with operation result
     */
    @PostMapping("/operators")
    public ResponseEntity<?> addUser(@RequestBody @Valid UserRegistrationRequestDto request) {
        logger.info("Request received: {}", request);

        // Create the operator user
        User operator = userService.createUser(
                request.getName(),
                request.getNic(),
                request.getEmail(),
                request.getPhoto(),
                UserRole.OPERATOR);

        return new ResponseEntityBuilder.Builder<Map<String, Object>>()
                .resultMessage("User added successfully")
                .httpStatus(HttpStatus.CREATED)
                .body(Map.of("operatorId", operator.getId()))
                .buildWrapped();
    }

    /**
     * Retrieves all operators.
     * 
     * @return response entity with list of all operators
     */
    @GetMapping("/operators")
    public ResponseEntity<?> getAllUsers() {
        List<User> operators = userService.getUsersByRole(UserRole.OPERATOR);
        List<UserListDto> operatorDtos = operators.stream()
                .map(user -> {
                    UserListDto dto = new UserListDto(user);
                    // Set photo as public URL if exists
                    String publicPhotoUrl = userService.getPublicPhotoUrl(user.getPhotoUrl());
                    if (publicPhotoUrl != null) {
                        dto.setPhoto(publicPhotoUrl);
                    }
                    return dto;
                })
                .collect(Collectors.toList());

        return new ResponseEntityBuilder.Builder<List<UserListDto>>()
                .resultMessage("List of all operators retrieved successfully")
                .httpStatus(HttpStatus.OK)
                .body(operatorDtos)
                .buildWrapped();
    }

    /**
     * Retrieves an operator by their ID.
     * 
     * @param id the UUID of the operator
     * @return response entity with operator details
     */
    @GetMapping("/operators/{id}")
    public ResponseEntity<?> getUserById(@PathVariable UUID id) {
        User user = userService.getUserById(id);
        userService.validateUserRole(id, UserRole.OPERATOR);

        // Create operator DTO from entity
        UserDto operatorDto = new UserDto(user);

        // Set photo as public URL if exists
        String publicPhotoUrl = userService.getPublicPhotoUrl(user.getPhotoUrl());
        if (publicPhotoUrl != null) {
            operatorDto.setPhoto(publicPhotoUrl);
        }

        return new ResponseEntityBuilder.Builder<Map<String, Object>>()
                .resultMessage("User details retrieved successfully")
                .httpStatus(HttpStatus.OK)
                .body(Map.of("operator", operatorDto))
                .buildWrapped();
    }

    /**
     * Edits an existing operator.
     * 
     * @param id      the UUID of the operator to edit
     * @param request the updated operator data
     * @return response entity with operation result
     */
    @PutMapping("/operators/{id}")
    public ResponseEntity<?> editUser(@PathVariable UUID id,
            @RequestBody @Valid UserRegistrationRequestDto request) {
        logger.info("Request received: {}", request);

        userService.validateUserRole(id, UserRole.OPERATOR);

        // Update user fields
        User user = userService.updateUser(id, request.getName(), request.getNic(), request.getEmail(),
                request.getPhoto());

        // Create response DTO with related data
        UserDto operatorDto = new UserDto(user);

        // Set photo as public URL if exists
        String publicPhotoUrl = userService.getPublicPhotoUrl(user.getPhotoUrl());
        if (publicPhotoUrl != null) {
            operatorDto.setPhoto(publicPhotoUrl);
        }

        return new ResponseEntityBuilder.Builder<Map<String, Object>>()
                .resultMessage("User updated successfully")
                .httpStatus(HttpStatus.OK)
                .body(Map.of("operator", operatorDto))
                .buildWrapped();
    }

    /**
     * Changes the status of an operator.
     * 
     * @param id        the UUID of the operator
     * @param newStatus the new status to set
     * @return response entity with operation result
     */
    @PutMapping("/operators/{id}/status")
    public ResponseEntity<?> changeUserStatus(@PathVariable UUID id, @RequestBody String newStatus) {
        userService.changeUserStatusByRole(id, newStatus, UserRole.OPERATOR);
        return new ResponseEntityBuilder.Builder<Map<String, Object>>()
                .resultMessage("User status updated successfully")
                .httpStatus(HttpStatus.OK)
                .body(Map.of("updated", true))
                .buildWrapped();
    }

    /**
     * Deletes an operator by their ID.
     * 
     * @param id the UUID of the operator to delete
     * @return response entity with operation result
     */
    @DeleteMapping("/operators/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable UUID id) {
        userService.deleteUserByRole(id, UserRole.OPERATOR);
        return new ResponseEntityBuilder.Builder<Map<String, Object>>()
                .resultMessage("User deleted successfully")
                .httpStatus(HttpStatus.OK)
                .body(Map.of("deleted", true))
                .buildWrapped();
    }

    /**
     * Retrieves the total count of operators.
     * 
     * @return response entity with operator count
     */
    @GetMapping("/operators/count")
    public ResponseEntity<?> getUserCount() {
        long countTotal = userService.getUserCountByRole(UserRole.OPERATOR);
        long countActive = userService.getUserCountByRoleAndStatus(UserRole.OPERATOR, User.UserStatus.ACTIVE);
        long countBlocked = userService.getUserCountByRoleAndStatus(UserRole.OPERATOR, User.UserStatus.BLOCKED);
        return new ResponseEntityBuilder.Builder<Map<String, Object>>()
                .resultMessage("Total number of operators retrieved successfully")
                .httpStatus(HttpStatus.OK)
                .body(Map.of(
                        "total", countTotal,
                        "active", countActive,
                        "blocked", countBlocked))
                .buildWrapped();
    }
}
