package com.canpay.api.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

@Configuration
@ConfigurationProperties(prefix = "application.jwt")
public class JwtConfig {
    private String secretKey;
    private String tokenPrefix;
 //   private Integer tokenExpirationAfterDays;
    private Integer tokenExpirationAfterMinutes; // Changed from tokenExpirationAfterDays

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

//    correct one
//    public Integer getTokenExpirationAfterDays() {
//        return tokenExpirationAfterDays;
//    }

//    public void setTokenExpirationAfterDays(Integer tokenExpirationAfterDays) {
//        this.tokenExpirationAfterDays = tokenExpirationAfterDays;
//    }

//this is correct one
//    public void setTokenExpirationAfterDays(Integer tokenExpirationAfterDays) {
//        this.tokenExpirationAfterDays = tokenExpirationAfterDays != null ? tokenExpirationAfterDays : 1; // Default to 1 day
//    }


    public Integer getTokenExpirationAfterMinutes() {
        return tokenExpirationAfterMinutes;
    }

    public void setTokenExpirationAfterMinutes(Integer tokenExpirationAfterMinutes) {
        this.tokenExpirationAfterMinutes = tokenExpirationAfterMinutes != null ? tokenExpirationAfterMinutes : 5; // Default to 5 minutes
    }

    public String getAuthorizationHeader(){
        return HttpHeaders.AUTHORIZATION;
    }
}
