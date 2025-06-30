package com.canpay.api.service.implementation;

import com.canpay.api.auth.ApplicationUserDetails;
import com.canpay.api.entity.User;
import com.canpay.api.repository.UserRepository;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class ApplicationUserServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public ApplicationUserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Deprecated: Used only for default Spring Security calls
        throw new UnsupportedOperationException("Use loadUserByUsername(String email, UserRole role) instead");
    }

    public UserDetails loadUserByUsername(String email, User.UserRole role) throws UsernameNotFoundException {
        return userRepository.findByEmailAndRole(email, role)
                .map(ApplicationUserDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("User not found for email: " + email + " and role: " + role));
    }

}
