package com.canpay.api.service;

import com.canpay.api.entity.User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface UserSevice {
    Optional<User> findByPhone(String phone);
    User createPassenger(String phone, String name);
}
