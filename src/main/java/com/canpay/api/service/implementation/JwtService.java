package com.canpay.api.service.implementation;

import com.canpay.api.entity.User;
import com.canpay.api.jwt.JwtConfig;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.SignatureAlgorithm;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;


@Service
public class JwtService {

    private final JwtConfig jwtConfig;
    private final Key key;

    public JwtService(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
        this.key = Keys.hmacShaKeyFor(jwtConfig.getSecretKey().getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(User user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtConfig.getTokenExpirationAfterDays() * 24 * 60 * 60 * 1000L);

        return Jwts.builder()
                .setSubject(user.getEmail())
                // .claim("nic", expiryDate)
                .claim("role", user.getRole())
                .claim("name", user.getName())
                .claim("id", user.getId())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
}

