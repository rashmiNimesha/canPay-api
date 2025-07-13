package com.canpay.api.service.implementation;

import com.canpay.api.entity.User;
import com.canpay.api.jwt.JwtConfig;
import com.canpay.api.repository.UserRepository;
import io.jsonwebtoken.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {
    private final JwtConfig jwtConfig;
    private final PrivateKey privateKey;
    private final PublicKey publicKey;
    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);

    @Autowired
    private UserRepository userRepository;

    public JwtService(JwtConfig jwtConfig, UserRepository userRepository) {
        logger.info("JwtService initialized with RSA keys....");
        this.jwtConfig = jwtConfig;
        this.privateKey = jwtConfig.getPrivateKey();
        this.publicKey = jwtConfig.getPublicKey();
        this.userRepository = userRepository;
        logger.info("JwtService initialized with RSA keys.");
    }

    public String generateToken(User user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtConfig.getTokenExpirationAfterMinutes() * 60 * 1000L);
        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("role", user.getRole())
                .claim("name", user.getName())
                .claim("id", user.getId())
                .claim("nic", user.getNic())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();
    }

    public String extractEmail(String token) {
        return extractAllClaims(token).getSubject();
    }

    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }

    public UUID extractUserId(String token) {
        return UUID.fromString(extractAllClaims(token).get("id", String.class));
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(publicKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            logger.error("Failed to parse JWT: {}", e.getMessage());
            throw new RuntimeException("Invalid JWT token", e);
        }
    }

    public boolean isTokenValid(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return !claims.getExpiration().before(new Date());
        } catch (Exception e) {
            logger.error("Invalid token: {}", e.getMessage());
            return false;
        }
    }
}
