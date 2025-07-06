package com.canpay.api.service.dashboard;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.canpay.api.entity.User;
import com.canpay.api.entity.User.UserRole;
import com.canpay.api.entity.User.UserStatus;
import com.canpay.api.lib.Utils;
import com.canpay.api.repository.dashboard.DUserRepository;

/**
 * Service for managing User entities in the dashboard context.
 * Handles user creation, updating, photo management, and uniqueness
 * validations for all user roles.
 */
@Service
public class DUserService {

    private final DUserRepository userRepository;

    // Base URL for image links, set in application.properties as app.base-url
    @Value("${app.base-url}")
    private String baseUrl;

    @Autowired
    public DUserService(DUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Creates a new user with the specified role.
     * Validates required fields and uniqueness of NIC and Email within the role.
     */
    @Transactional
    public User createUser(String name, String nic, String email, String photo, UserRole role) {
        if (name == null || name.isBlank() || nic == null || nic.isBlank() || email == null || email.isBlank()) {
            throw new IllegalArgumentException("Name, NIC, and Email Address are required.");
        }
        checkNicUnique(nic, role, null);
        checkEmailUnique(email, role, null);

        User user = new User();
        user.setName(name);
        user.setNic(nic);
        user.setEmail(email);
        user.setRole(role);
        user.setStatus(UserStatus.ACTIVE);
        user.setPhotoUrl(handlePhotoUpload(photo, null));

        return userRepository.save(user);
    }

    /**
     * Updates an existing user's basic information.
     * Validates uniqueness constraints and handles photo updates.
     */
    @Transactional
    public User updateUser(UUID userId, String name, String nic, String email, String photo) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (name != null && !name.isBlank())
            user.setName(name);

        if (nic != null && !nic.isBlank() && !user.getNic().equals(nic)) {
            checkNicUnique(nic, user.getRole(), userId);
            user.setNic(nic);
        }

        if (email != null && !email.isBlank() && !user.getEmail().equals(email)) {
            checkEmailUnique(email, user.getRole(), userId);
            user.setEmail(email);
        }

        user.setPhotoUrl(handlePhotoUpload(photo, user.getPhotoUrl()));

        return userRepository.save(user);
    }

    /**
     * Changes the status of a user by ID.
     */
    @Transactional
    public void changeUserStatus(UUID userId, UserStatus newStatus) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setStatus(newStatus);
        userRepository.save(user);
    }

    /**
     * Deletes a user by ID and their associated photo.
     */
    @Transactional
    public void deleteUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Delete the associated photo
        Utils.deleteImage(user.getPhotoUrl());

        userRepository.deleteById(userId);
    }

    /**
     * Converts a photo URL to a public URL format.
     */
    public String getPublicPhotoUrl(String photoUrl) {
        if (photoUrl != null && !photoUrl.isBlank()) {
            String filename = Paths.get(photoUrl).getFileName().toString();
            return baseUrl + "/" + filename;
        }
        return null;
    }

    /**
     * Gets a user by ID.
     */
    public User getUserById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    /**
     * Gets the count of users by role.
     */
    public long getUserCountByRole(UserRole role) {
        return userRepository.countByRole(role);
    }

    /**
     * Gets all users by role.
     */
    public List<User> getUsersByRole(UserRole role) {
        return userRepository.findByRole(role);
    }

    /**
     * Validates user role for operations.
     */
    public void validateUserRole(UUID userId, UserRole expectedRole) {
        User user = getUserById(userId);
        if (user.getRole() != expectedRole) {
            throw new IllegalArgumentException("User is not a " + expectedRole.name().toLowerCase());
        }
    }

    /**
     * Changes user status with role validation.
     */
    @Transactional
    public void changeUserStatusByRole(UUID userId, String newStatus, UserRole expectedRole) {
        validateUserRole(userId, expectedRole);

        String cleanStatus = newStatus.replace("\"", "").trim();

        // Validate newStatus against UserStatus enum
        try {
            UserStatus status = Arrays.stream(UserStatus.values())
                    .filter(enumValue -> enumValue.name().equalsIgnoreCase(cleanStatus))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Invalid user status provided."));

            changeUserStatus(userId, status);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid user status provided.");
        }
    }

    /**
     * Deletes user with role validation.
     */
    @Transactional
    public void deleteUserByRole(UUID userId, UserRole expectedRole) {
        validateUserRole(userId, expectedRole);
        deleteUser(userId);
    }

    /*
     * --------------------- HELPER METHODS ---------------------
     */

    /**
     * Checks if a user with the given NIC and role exists, excluding the specified
     * user ID.
     * Throws an exception if a duplicate is found.
     */
    private void checkNicUnique(String nic, UserRole role, UUID excludeUserId) {
        userRepository.findByNicAndRole(nic, role).ifPresent(user -> {
            if (excludeUserId == null || !user.getId().equals(excludeUserId)) {
                throw new IllegalArgumentException("NIC already exists for another " + role.name().toLowerCase() + ".");
            }
        });
    }

    /**
     * Checks if a user with the given email and role exists, excluding the
     * specified user ID.
     * Throws an exception if a duplicate is found.
     */
    private void checkEmailUnique(String email, UserRole role, UUID excludeUserId) {
        userRepository.findByEmailAndRole(email, role).ifPresent(user -> {
            if (excludeUserId == null || !user.getId().equals(excludeUserId)) {
                throw new IllegalArgumentException(
                        "Email Address already exists for another " + role.name().toLowerCase() + ".");
            }
        });
    }

    /**
     * Handles photo upload logic.
     * If the photo is a URL, it returns the old URL.
     * If the photo is a base64 string, it saves it and returns the new URL.
     * If the photo is null or blank, it returns the old URL.
     */
    private String handlePhotoUpload(String photo, String oldPhotoUrl) {
        if (photo == null || photo.isBlank())
            return oldPhotoUrl;
        if (photo.startsWith("http") || (baseUrl != null && photo.startsWith(baseUrl))) {
            return oldPhotoUrl; // Already a URL, don't update
        }
        try {
            if (oldPhotoUrl != null) {
                Utils.deleteImage(oldPhotoUrl);
            }
            return Utils.saveImage(photo, UUID.randomUUID().toString() + ".jpg");
        } catch (IOException e) {
            throw new RuntimeException("Failed to save user photo", e);
        }
    }
}
