package com.canpay.api.service;

import com.canpay.api.entity.User;
import com.canpay.api.entity.User.UserRole;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface UserSevice {
    Optional<User> findByEmail(String email);

    @Transactional
    User findUserByEmail(String email);

    @Transactional
    User registerWithEmail(String email, String roleString);

    @Transactional
    User updatePassengerProfile(String email, String name, String nic, String accName, String bank, long accNo, UserRole role);

    @Transactional
    User updateOperatorProfile(String email, String name, String nic, String profileImage, UserRole role);

    @Transactional
    User updateOwnerProfile(String email, String name, String nic, String profileImage, String accName, String bank, long accNo, UserRole role);

}
