package com.canpay.api.auth;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import com.canpay.api.entity.User;
import com.canpay.api.entity.User.UserRole;

import java.util.Collection;
import java.util.Collections;

public class ApplicationUserDetails implements UserDetails {

    private final User user;

    public ApplicationUserDetails(User user) {
        this.user = user;
    }

    public String getEmail() {
        return user.getEmail();
    }

    public UserRole getRole() {
        return user.getRole();
    }

    public User getUser() {
        return user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority("ROLE_" + user.getRole()));
    }

    @Override
    public String getPassword() {
        return ""; // OTP-based, no password
    }

    @Override
    public String getUsername() {
        return user.getEmail(); // Email is used as username
    }

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}

