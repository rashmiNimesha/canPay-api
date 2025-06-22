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
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User createPassenger(String email, String name) {
        User user = new User();
        user.setEmail(email);
        user.setName(name);
        user.setRole("PASSENGER");
        return userRepository.save(user);
    }

    @Override
    public Optional<User> setPin(String email, String pin) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        userOpt.ifPresent(user -> {
            user.setPin(pin);
            userRepository.save(user);
        });
        return userOpt;
    }
}
