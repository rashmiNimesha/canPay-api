package com.canpay.api.controller.canpayadmin;

import com.canpay.api.dto.dashboard.DBankAccountDto;
import com.canpay.api.dto.dashboard.user.UserDto;
import com.canpay.api.dto.dashboard.user.UserListDto;
import com.canpay.api.dto.dashboard.user.UserListWalletDto;
import com.canpay.api.dto.dashboard.user.UserRegistrationRequestDto;
import com.canpay.api.dto.dashboard.user.UserWalletDto;
import com.canpay.api.entity.ResponseEntityBuilder;
import com.canpay.api.entity.User;
import com.canpay.api.entity.User.UserRole;
import com.canpay.api.entity.User.UserStatus;
import com.canpay.api.entity.Wallet;
import com.canpay.api.entity.Wallet.WalletType;
import com.canpay.api.service.dashboard.DUserService;
import com.canpay.api.service.dashboard.DWalletService;
import com.canpay.api.service.dashboard.DBankAccountService;
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
 * REST controller for managing passengers in the CanPay admin dashboard.
 */
@RestController
@RequestMapping("/api/v1/canpay-admin")
public class PassengerController {
    private static final Logger logger = LoggerFactory.getLogger(PassengerController.class);

    private final DUserService userService;
    private final DWalletService walletService;
    private final DBankAccountService bankAccountService;

    /**
     * Constructor for PassengerController.
     * 
     * @param userService        the service handling user operations
     * @param walletService      the service handling wallet operations
     * @param bankAccountService the service handling bank account operations
     */
    @Autowired
    public PassengerController(DUserService userService, DWalletService walletService,
            DBankAccountService bankAccountService) {
        this.userService = userService;
        this.walletService = walletService;
        this.bankAccountService = bankAccountService;
    }

    /**
     * Adds a new passenger.
     * 
     * @param request the passenger registration request data
     * @return response entity with operation result
     */
    @PostMapping("/passengers")
    public ResponseEntity<?> addPassenger(@RequestBody @Valid UserRegistrationRequestDto request) {
        logger.info("Request received: {}", request);

        // Create the passenger user
        User passenger = userService.createUser(
                request.getName(),
                request.getNic(),
                request.getEmail(),
                request.getPhoto(),
                UserRole.PASSENGER);

        // Create and associate a wallet for the passenger
        Wallet passengerWallet = walletService.createWallet(passenger, WalletType.PASSENGER);
        passenger.setWallet(passengerWallet);

        // Create bank accounts if provided
        System.out.println("Bank accounts provided: " + request.getBankAccounts().size());
        if (request.getBankAccounts() != null && !request.getBankAccounts().isEmpty()) {
            bankAccountService.createBankAccounts(passenger, request.getBankAccounts());
        }

        return new ResponseEntityBuilder.Builder<Map<String, Object>>()
                .resultMessage("Passenger added successfully")
                .httpStatus(HttpStatus.CREATED)
                .body(Map.of("passengerId", passenger.getId()))
                .buildWrapped();

    }

    /**
     * Retrieves all passengers.
     * 
     * @return response entity with list of all passengers
     */
    @GetMapping("/passengers")
    public ResponseEntity<?> getAllPassengers() {
        List<User> passengers = userService.getUsersByRole(UserRole.PASSENGER);
        List<UserListDto> passengerDtos = passengers.stream()
                .map(user -> {
                    UserListDto dto = new UserListDto(user);
                    // Set photo as public URL if exists
                    String publicPhotoUrl = userService.getPublicPhotoUrl(user.getPhotoUrl());
                    if (publicPhotoUrl != null) {
                        dto.setPhoto(publicPhotoUrl);
                    }
                    // Get wallet information
                    UserListWalletDto walletDto = walletService.getWalletByUserId(user.getId())
                            .filter(wallet -> wallet.getType() == WalletType.PASSENGER)
                            .map(UserListWalletDto::new)
                            .orElse(null);
                    dto.setWallet(walletDto);
                    return dto;
                })
                .collect(Collectors.toList());

        return new ResponseEntityBuilder.Builder<List<UserListDto>>()
                .resultMessage("List of all passengers retrieved successfully")
                .httpStatus(HttpStatus.OK)
                .body(passengerDtos)
                .buildWrapped();
    }

    /**
     * Retrieves a passenger by their ID.
     * 
     * @param id the UUID of the passenger
     * @return response entity with passenger details
     */
    @GetMapping("/passengers/{id}")
    public ResponseEntity<?> getPassengerById(@PathVariable UUID id) {
        User user = userService.getUserById(id);
        userService.validateUserRole(id, UserRole.PASSENGER);

        // Create passenger DTO from entity
        UserDto passengerDto = new UserDto(user);

        // Set photo as public URL if exists
        String publicPhotoUrl = userService.getPublicPhotoUrl(user.getPhotoUrl());
        if (publicPhotoUrl != null) {
            passengerDto.setPhoto(publicPhotoUrl);
        }

        // Fetch and set bank accounts
        List<DBankAccountDto> bankAccounts = bankAccountService.getBankAccountsByUserId(user.getId());
        passengerDto.setBankAccounts(bankAccounts);

        // Fetch and set wallet
        UserWalletDto wallet = walletService.getWalletByUserId(user.getId())
                .filter(w -> w.getType() == WalletType.PASSENGER)
                .map(UserWalletDto::new)
                .orElse(null);
        passengerDto.setWallet(wallet);

        return new ResponseEntityBuilder.Builder<Map<String, Object>>()
                .resultMessage("Passenger details retrieved successfully")
                .httpStatus(HttpStatus.OK)
                .body(Map.of("passenger", passengerDto))
                .buildWrapped();
    }

    /**
     * Edits an existing passenger.
     * 
     * @param id      the UUID of the passenger to edit
     * @param request the updated passenger data
     * @return response entity with operation result
     */
    @PutMapping("/passengers/{id}")
    public ResponseEntity<?> editPassenger(@PathVariable UUID id,
            @RequestBody @Valid UserRegistrationRequestDto request) {
        logger.info("Request received: {}", request);

        userService.validateUserRole(id, UserRole.PASSENGER);

        // Update user fields
        User user = userService.updateUser(id, request.getName(), request.getNic(), request.getEmail(),
                request.getPhoto());

        // Ensure passenger has a wallet
        walletService.ensureUserWallet(user, WalletType.PASSENGER);

        // Replace bank accounts if provided
        if (request.getBankAccounts() != null) {
            bankAccountService.replaceBankAccounts(user, request.getBankAccounts());
        }

        // Create response DTO with related data
        UserDto passengerDto = new UserDto(user);

        // Set photo as public URL if exists
        String publicPhotoUrl = userService.getPublicPhotoUrl(user.getPhotoUrl());
        if (publicPhotoUrl != null) {
            passengerDto.setPhoto(publicPhotoUrl);
        }

        // Fetch and set bank accounts
        List<DBankAccountDto> bankAccounts = bankAccountService.getBankAccountsByUserId(user.getId());
        passengerDto.setBankAccounts(bankAccounts);

        // Set wallet
        UserWalletDto wallet = walletService.getWalletByUserId(user.getId())
                .filter(w -> w.getType() == WalletType.PASSENGER)
                .map(UserWalletDto::new)
                .orElse(null);
        passengerDto.setWallet(wallet);

        return new ResponseEntityBuilder.Builder<Map<String, Object>>()
                .resultMessage("Passenger updated successfully")
                .httpStatus(HttpStatus.OK)
                .body(Map.of("passenger", passengerDto))
                .buildWrapped();
    }

    /**
     * Changes the status of a passenger.
     * 
     * @param id        the UUID of the passenger
     * @param newStatus the new status to set
     * @return response entity with operation result
     */
    @PutMapping("/passengers/{id}/status")
    public ResponseEntity<?> changePassengerStatus(@PathVariable UUID id, @RequestBody String newStatus) {
        userService.changeUserStatusByRole(id, newStatus, UserRole.PASSENGER);
        return new ResponseEntityBuilder.Builder<Map<String, Object>>()
                .resultMessage("Passenger status updated successfully")
                .httpStatus(HttpStatus.OK)
                .body(Map.of("updated", true))
                .buildWrapped();
    }

    /**
     * Deletes a passenger by their ID.
     * 
     * @param id the UUID of the passenger to delete
     * @return response entity with operation result
     */
    @DeleteMapping("/passengers/{id}")
    public ResponseEntity<?> deletePassenger(@PathVariable UUID id) {
        userService.deleteUserByRole(id, UserRole.PASSENGER);
        return new ResponseEntityBuilder.Builder<Map<String, Object>>()
                .resultMessage("Passenger deleted successfully")
                .httpStatus(HttpStatus.OK)
                .body(Map.of("deleted", true))
                .buildWrapped();
    }

    /**
     * Retrieves the total count of passengers.
     * 
     * @return response entity with passenger count
     */
    @GetMapping("/passengers/count")
    public ResponseEntity<?> getPassengerCount() {
        long countTotal = userService.getUserCountByRole(UserRole.PASSENGER);
        long countActive = userService.getUserCountByRoleAndStatus(UserRole.PASSENGER, UserStatus.ACTIVE);
        long countBlocked = userService.getUserCountByRoleAndStatus(UserRole.PASSENGER, UserStatus.BLOCKED);
        return new ResponseEntityBuilder.Builder<Map<String, Object>>()
                .resultMessage("Total number of passengers retrieved successfully")
                .httpStatus(HttpStatus.OK)
                .body(Map.of(
                        "total", countTotal,
                        "active", countActive,
                        "blocked", countBlocked))
                .buildWrapped();
    }
}
