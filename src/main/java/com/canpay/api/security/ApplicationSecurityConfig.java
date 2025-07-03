package com.canpay.api.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.canpay.api.jwt.JwtAuthFilter;

import java.util.List;

/**
 * Configuration class for defining multiple security filter chains.
 * Enables method-level security and configures CORS, CSRF, and session
 * management.
 */
@Configuration
@EnableMethodSecurity
public class ApplicationSecurityConfig {

        private final JwtAuthFilter jwtAuthFilter;

        /**
         * Constructor for DualSecurityConfig.
         *
         * @param jwtAuthFilter Custom JWT authentication filter.
         */
        public ApplicationSecurityConfig(JwtAuthFilter jwtAuthFilter) {
                this.jwtAuthFilter = jwtAuthFilter;
        }

        /**
         * Security filter chain for admin endpoints.
         * Configures JWT-based authentication for requests to
         * "/api/v1/canpay-admin/**".
         *
         * @param http    HttpSecurity instance for configuring security.
         * @param issuer  Issuer URI for validating JWTs.
         * @param jwksUri JWKS URI for fetching public keys.
         * @return Configured SecurityFilterChain.
         * @throws Exception If an error occurs during configuration.
         */
        @Bean
        @Order(1)
        public SecurityFilterChain adminChain(HttpSecurity http,
                        @Value("${CLERK_ISSUER_URI}") String issuer,
                        @Value("${CLERK_JWKS_URI}") String jwksUri)
                        throws Exception {

                var jwtConverter = new JwtAuthenticationConverter();
                jwtConverter.setJwtGrantedAuthoritiesConverter(new AdminClaimConverter());

                http
                                .securityMatcher("/api/v1/canpay-admin/**")
                                .csrf(csrf -> csrf.disable())
                                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers("/api/v1/canpay-admin/**").hasRole("ADMIN")
                                                .anyRequest().denyAll())
                                .oauth2ResourceServer(oauth2 -> oauth2
                                                .jwt(jwt -> jwt
                                                                .jwkSetUri(jwksUri)
                                                                .jwtAuthenticationConverter(jwtConverter)));

                return http.build();
        }

        /**
         * Security filter chain for mobile endpoints.
         * Configures custom JwtAuthFilter for requests to "/api/v1/**".
         *
         * @param http HttpSecurity instance for configuring security.
         * @return Configured SecurityFilterChain.
         * @throws Exception If an error occurs during configuration.
         */
        @Bean
        @Order(2)
        public SecurityFilterChain mobileChain(HttpSecurity http) throws Exception {
                http
                                .securityMatcher("/api/v1/**")
                                .csrf(csrf -> csrf.disable())
                                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers("/api/v1/auth/**").permitAll()
                                                .anyRequest().authenticated())
                                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }

        /**
         * Configures CORS settings for the application.
         * Allows specific origins, HTTP methods, and headers.
         *
         * @return Configured CorsConfigurationSource.
         */
        @Bean
        public CorsConfigurationSource corsConfigurationSource() {
                CorsConfiguration configuration = new CorsConfiguration();

                // Allowed origin list
                configuration.setAllowedOriginPatterns(
                                List.of("http://localhost:3000", "https://dash.canpay.sehanw.com"));

                // Include all necessary HTTP methods
                configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));

                // Allow only necessary headers
                configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept", "Origin"));

                // Required for cookies/auth headers
                configuration.setAllowCredentials(true);

                // Cache preflight requests
                configuration.setMaxAge(3600L);

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", configuration);
                return source;
        }
}