package com.canpay.api.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.annotation.PostConstruct;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Configuration
@ConfigurationProperties(prefix = "application.jwt")
public class JwtConfig {
    private static final Logger logger = LoggerFactory.getLogger(JwtConfig.class);
    private String secretKey;
    private String tokenPrefix;
    private Integer tokenExpirationAfterMinutes;
    private String privateKeyBase64;
    private String publicKeyBase64;
    private PrivateKey privateKey;
    private PublicKey publicKey;

    public JwtConfig() {
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getTokenPrefix() {
        return tokenPrefix;
    }

    public void setTokenPrefix(String tokenPrefix) {
        this.tokenPrefix = tokenPrefix;
    }

    public Integer getTokenExpirationAfterMinutes() {
        return tokenExpirationAfterMinutes;
    }

    public void setTokenExpirationAfterMinutes(Integer tokenExpirationAfterMinutes) {
        this.tokenExpirationAfterMinutes = tokenExpirationAfterMinutes != null ? tokenExpirationAfterMinutes : 5; // Default
        // to
        // 5
        // minutes
    }

    public String getAuthorizationHeader() {
        return HttpHeaders.AUTHORIZATION;
    }

    public String getPrivateKeyBase64() {
        return privateKeyBase64;
    }

    public void setPrivateKeyBase64(String privateKeyBase64) {
        this.privateKeyBase64 = privateKeyBase64;
    }

    public String getPublicKeyBase64() {
        return publicKeyBase64;
    }

    public void setPublicKeyBase64(String publicKeyBase64) {
        this.publicKeyBase64 = publicKeyBase64;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    @PostConstruct
    public void loadKeys() {
        try {
            if (privateKeyBase64 != null && !privateKeyBase64.isEmpty()) {
                byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyBase64);
                PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
                KeyFactory kf = KeyFactory.getInstance("RSA");
                this.privateKey = kf.generatePrivate(keySpec);
                logger.info("RSA private key loaded successfully.");
            }
            if (publicKeyBase64 != null && !publicKeyBase64.isEmpty()) {
                byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyBase64);
                X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
                KeyFactory kf = KeyFactory.getInstance("RSA");
                this.publicKey = kf.generatePublic(keySpec);
                logger.info("RSA public key loaded successfully.");
            }
        } catch (Exception e) {
            logger.error("Failed to load RSA keys: {}", e.getMessage());
            throw new RuntimeException("Failed to load RSA keys", e);
        }
    }
}
