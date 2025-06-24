package com.canpay.api.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;

@Service
public class JwtUtil {

    private final JwtConfig jwtConfig;
    private final Key key;

    public JwtUtil(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
        this.key = Keys.hmacShaKeyFor(jwtConfig.getSecretKey().getBytes(StandardCharsets.UTF_8));
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token.replace(jwtConfig.getTokenPrefix(), ""))
                .getBody();
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }

    public boolean isTokenValid(String token) {
        try {
            extractAllClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
