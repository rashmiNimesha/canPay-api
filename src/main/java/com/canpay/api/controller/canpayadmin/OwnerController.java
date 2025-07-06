package com.canpay.api.controller.canpayadmin;

import com.canpay.api.dto.Dashboard.User.UserDto;
import com.canpay.api.dto.Dashboard.User.UserListDto;
import com.canpay.api.dto.Dashboard.User.UserListWalletDto;
import com.canpay.api.dto.Dashboard.User.UserRegistrationRequestDto;
import com.canpay.api.dto.Dashboard.User.UserWalletDto;
import com.canpay.api.dto.Dashboard.DBankAccountDto;
import com.canpay.api.entity.ResponseEntityBuilder;
import com.canpay.api.entity.User;
import com.canpay.api.entity.User.UserRole;
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
 * REST controller for managing owners in the CanPay admin dashboard.
 */
@RestController
@RequestMapping("/api/v1/canpay-admin")
public class OwnerController {
    private static final Logger logger = LoggerFactory.getLogger(OwnerController.class);

    private final DUserService userService;
    private final DWalletService walletService;
    private final DBankAccountService bankAccountService;

    /**
     * Constructor for UserController.
     * 
     * @param userService        the service handling user operations
     * @param walletService      the service handling wallet operations
     * @param bankAccountService the service handling bank account operations
     */
    @Autowired
    public OwnerController(DUserService userService, DWalletService walletService,
            DBankAccountService bankAccountService) {
        this.userService = userService;
        this.walletService = walletService;
        this.bankAccountService = bankAccountService;
    }

    /**
     * Adds a new owner.
     * 
     * @param request the owner registration request data
     * @return response entity with operation result
     */
    @PostMapping("/owners")
    public ResponseEntity<?> addUser(@RequestBody @Valid UserRegistrationRequestDto request) {
        logger.info("Request received: {}", request);

        // Create the owner user
        User owner = userService.createUser(
                request.getName(),
                request.getNic(),
                request.getEmail(),
                request.getPhoto(),
                UserRole.OWNER);

        // Create and associate a wallet for the owner
        Wallet ownerWallet = walletService.createWallet(owner, WalletType.OWNER);
        owner.setWallet(ownerWallet);

        // Create bank accounts if provided
        System.out.println("Bank accounts provided: " + request.getBankAccounts().size());
        if (request.getBankAccounts() != null && !request.getBankAccounts().isEmpty()) {
            bankAccountService.createBankAccounts(owner, request.getBankAccounts());
        }

        return new ResponseEntityBuilder.Builder<Map<String, Object>>()
                .resultMessage("User added successfully")
                .httpStatus(HttpStatus.CREATED)
                .body(Map.of("ownerId", owner.getId()))
                .buildWrapped();

    }

    /**
     * Retrieves all owners.
     * 
     * @return response entity with list of all owners
     */
    @GetMapping("/owners")
    public ResponseEntity<?> getAllUsers() {
        List<User> owners = userService.getUsersByRole(UserRole.OWNER);
        List<UserListDto> ownerDtos = owners.stream()
                .map(user -> {
                    UserListDto dto = new UserListDto(user);
                    // Set photo as public URL if exists
                    String publicPhotoUrl = userService.getPublicPhotoUrl(user.getPhotoUrl());
                    if (publicPhotoUrl != null) {
                        dto.setPhoto(publicPhotoUrl);
                    }
                    // Get wallet information
                    UserListWalletDto walletDto = walletService.getWalletByUserId(user.getId())
                            .filter(wallet -> wallet.getType() == WalletType.OWNER)
                            .map(UserListWalletDto::new)
                            .orElse(null);
                    dto.setWallet(walletDto);
                    return dto;
                })
                .collect(Collectors.toList());

        return new ResponseEntityBuilder.Builder<List<UserListDto>>()
                .resultMessage("List of all owners retrieved successfully")
                .httpStatus(HttpStatus.OK)
                .body(ownerDtos)
                .buildWrapped();
    }

    /**
     * Retrieves an owner by their ID.
     * 
     * @param id the UUID of the owner
     * @return response entity with owner details
     */
    @GetMapping("/owners/{id}")
    public ResponseEntity<?> getUserById(@PathVariable UUID id) {
        User user = userService.getUserById(id);
        userService.validateUserRole(id, UserRole.OWNER);

        // Create owner DTO from entity
        UserDto ownerDto = new UserDto(user);

        // Set photo as public URL if exists
        String publicPhotoUrl = userService.getPublicPhotoUrl(user.getPhotoUrl());
        if (publicPhotoUrl != null) {
            ownerDto.setPhoto(publicPhotoUrl);
        }

        // Fetch and set bank accounts
        List<DBankAccountDto> bankAccounts = bankAccountService.getBankAccountsByUserId(user.getId());
        ownerDto.setBankAccounts(bankAccounts);

        // Fetch and set wallet
        UserWalletDto wallet = walletService.getWalletByUserId(user.getId())
                .filter(w -> w.getType() == WalletType.OWNER)
                .map(UserWalletDto::new)
                .orElse(null);
        ownerDto.setWallet(wallet);

        return new ResponseEntityBuilder.Builder<Map<String, Object>>()
                .resultMessage("User details retrieved successfully")
                .httpStatus(HttpStatus.OK)
                .body(Map.of("owner", ownerDto))
                .buildWrapped();
    }

    /**
     * Edits an existing owner.
     * 
     * @param id      the UUID of the owner to edit
     * @param request the updated owner data
     * @return response entity with operation result
     */
    @PutMapping("/owners/{id}")
    public ResponseEntity<?> editUser(@PathVariable UUID id,
            @RequestBody @Valid UserRegistrationRequestDto request) {
        logger.info("Request received: {}", request);

        userService.validateUserRole(id, UserRole.OWNER);

        // Update user fields
        User user = userService.updateUser(id, request.getName(), request.getNic(), request.getEmail(),
                request.getPhoto());

        // Ensure owner has a wallet
        walletService.ensureUserWallet(user, WalletType.OWNER);

        // Replace bank accounts if provided
        if (request.getBankAccounts() != null) {
            bankAccountService.replaceBankAccounts(user, request.getBankAccounts());
        }

        // Create response DTO with related data
        UserDto ownerDto = new UserDto(user);

        // Set photo as public URL if exists
        String publicPhotoUrl = userService.getPublicPhotoUrl(user.getPhotoUrl());
        if (publicPhotoUrl != null) {
            ownerDto.setPhoto(publicPhotoUrl);
        }

        // Fetch and set bank accounts
        List<DBankAccountDto> bankAccounts = bankAccountService.getBankAccountsByUserId(user.getId());
        ownerDto.setBankAccounts(bankAccounts);

        // Set wallet
        UserWalletDto wallet = walletService.getWalletByUserId(user.getId())
                .filter(w -> w.getType() == WalletType.OWNER)
                .map(UserWalletDto::new)
                .orElse(null);
        ownerDto.setWallet(wallet);

        return new ResponseEntityBuilder.Builder<Map<String, Object>>()
                .resultMessage("User updated successfully")
                .httpStatus(HttpStatus.OK)
                .body(Map.of("owner", ownerDto))
                .buildWrapped();
    }

    /**
     * Changes the status of an owner.
     * 
     * @param id        the UUID of the owner
     * @param newStatus the new status to set
     * @return response entity with operation result
     */
    @PutMapping("/owners/{id}/status")
    public ResponseEntity<?> changeUserStatus(@PathVariable UUID id, @RequestBody String newStatus) {
        userService.changeUserStatusByRole(id, newStatus, UserRole.OWNER);
        return new ResponseEntityBuilder.Builder<Map<String, Object>>()
                .resultMessage("User status updated successfully")
                .httpStatus(HttpStatus.OK)
                .body(Map.of("updated", true))
                .buildWrapped();
    }

    /**
     * Deletes an owner by their ID.
     * 
     * @param id the UUID of the owner to delete
     * @return response entity with operation result
     */
    @DeleteMapping("/owners/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable UUID id) {
        userService.deleteUserByRole(id, UserRole.OWNER);
        return new ResponseEntityBuilder.Builder<Map<String, Object>>()
                .resultMessage("User deleted successfully")
                .httpStatus(HttpStatus.OK)
                .body(Map.of("deleted", true))
                .buildWrapped();
    }

    /**
     * Retrieves the total count of owners.
     * 
     * @return response entity with owner count
     */
    @GetMapping("/owners/count")
    public ResponseEntity<?> getUserCount() {
        long count = userService.getUserCountByRole(UserRole.OWNER);
        return new ResponseEntityBuilder.Builder<Map<String, Object>>()
                .resultMessage("Total number of owners retrieved successfully")
                .httpStatus(HttpStatus.OK)
                .body(Map.of("ownerCount", count))
                .buildWrapped();
    }
}
