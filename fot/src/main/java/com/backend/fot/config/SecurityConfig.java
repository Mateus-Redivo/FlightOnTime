package com.backend.fot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security configuration for the FlightOnTime API.
 * 
 * <p><strong>Current Configuration:</strong> Development mode - all endpoints are publicly accessible.</p>
 * <p><strong>Production TODO:</strong> Implement proper authentication (JWT/OAuth2) and authorization.</p>
 * 
 * @author FlightOnTime Team
 * @version 1.0
 * @since 2025-12-18
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Configures HTTP security to permit all requests.
     * 
     * <p>This configuration is suitable for development and testing only.
     * For production, implement:</p>
     * <ul>
     *   <li>JWT or OAuth2 authentication</li>
     *   <li>API key validation</li>
     *   <li>Rate limiting</li>
     *   <li>Role-based access control (RBAC)</li>
     * </ul>
     * 
     * @param http the HttpSecurity to configure
     * @return the configured SecurityFilterChain
     * @throws Exception if an error occurs during configuration
     */
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()
            );
        
        return http.build();
    }
}
