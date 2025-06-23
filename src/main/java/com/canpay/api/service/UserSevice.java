package com.canpay.api.service;

import com.canpay.api.entity.BankAccount;
import com.canpay.api.entity.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface UserSevice {
    Optional<User> findByEmail(String email);
    User registerWithEmail(String email);
    User updateProfileWithBankAccount(String email, String name, String nic,
                                      String accName, String bank, long accNo );

}
