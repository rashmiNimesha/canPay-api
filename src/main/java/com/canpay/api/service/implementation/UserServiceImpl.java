package com.canpay.api.service.implementation;

import com.canpay.api.entity.User;
import com.canpay.api.repository.user.UserRepository;
import com.canpay.api.service.UserSevice;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class UserServiceImpl implements UserSevice {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<User> findByPhone(String phone) {
        return userRepository.findByPhone(phone);
    }

    @Override
    public User createPassenger(String phone, String name) {
        User user = new User();
        user.setPhone(phone);
        user.setName(name);
        user.setRole("PASSENGER");
        return userRepository.save(user);
    }
}
