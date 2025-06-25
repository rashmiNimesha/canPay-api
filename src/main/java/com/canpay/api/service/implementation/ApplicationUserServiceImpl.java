package com.canpay.api.service.implementation;

import com.canpay.api.auth.ApplicationUserDetails;
import com.canpay.api.repository.user.UserRepository;
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
        return userRepository.findByEmail(email)
                .map(ApplicationUserDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

}
