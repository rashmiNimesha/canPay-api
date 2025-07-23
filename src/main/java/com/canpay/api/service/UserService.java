package com.canpay.api.service;

import com.canpay.api.entity.BankAccount;
import com.canpay.api.entity.User;
import com.canpay.api.entity.User.UserRole;

import jakarta.transaction.Transactional;

import java.util.Optional;


public interface UserService {

    @Transactional
    Optional<User> findUserByEmail(String email);

    @Transactional
    Optional<User> findUserByEmailAndRole(String email, UserRole role);

    @Transactional
    User registerWithEmail(String email, String roleString);

    @Transactional
    User updatePassengerProfile(String email, String name, String nic, String accName, String bank, long accNo, UserRole role);

    @Transactional
    User updateOperatorProfile(String email, String name, String nic, String profileImage, UserRole role);

    @Transactional
    User updateOwnerProfile(String email, String name, String nic, String profileImage, String accName, String bank, long accNo, UserRole role);

    @Transactional
    BankAccount addBankAccount(String email, String accountName, String bankName, long accountNumber, boolean isDefault);

    User updateName(String email, String name);

    User updateEmail(String email, String newEmail);
}
