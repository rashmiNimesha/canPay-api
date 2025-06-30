package com.canpay.api.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;

public class AdminClaimConverter implements Converter<Jwt, Collection<GrantedAuthority>> {
    private static final Logger log = LoggerFactory.getLogger(AdminClaimConverter.class);

    /**
     * Converts a Jwt object into a collection of GrantedAuthority objects.
     *
     * This method checks the "isAdmin" claim in the JWT. If the claim is true,
     * it assigns the "ROLE_ADMIN" authority. Otherwise, it returns an empty
     * collection.
     *
     * @param jwt the Jwt object containing claims
     * @return a collection of GrantedAuthority objects
     */
    @Override
    public Collection<GrantedAuthority> convert(@NonNull Jwt jwt) {
        // Retrieve the "isAdmin" claim from the JWT.
        Boolean isAdmin = jwt.getClaim("isAdmin");

        // Log all claims for debugging purposes.
        log.info("JWT Claims: {}", jwt.getClaims());

        // If the "isAdmin" claim is true, return a collection with "ROLE_ADMIN".
        if (Boolean.TRUE.equals(isAdmin)) {
            return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }

        // Otherwise, return an empty collection.
        return List.of();
    }
}
