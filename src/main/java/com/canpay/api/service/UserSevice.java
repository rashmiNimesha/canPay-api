package com.canpay.api.service;

import com.canpay.api.entity.User;
import com.canpay.api.entity.User.UserRole;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface UserSevice {
    Optional<User> findByEmail(String email);
    User registerWithEmail(String email, String roleString);
    User updateProfileWithBankAccount(String email, String name, String nic,
                                      String accName, String bank, long accNo );

    User updateOperatorProfile(String email, String name, String nic, String profileImageBase64);
    User updateOwnerProfile(String email, String name, String nic, String profileImageBase64,
                                   String accountHolderName, String bankName, long accountNumber);
}
