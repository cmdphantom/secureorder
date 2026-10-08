package com.secureorder.config;

import com.secureorder.adapter.in.web.AuthController;
import com.secureorder.adapter.in.web.OrdersController;
import com.secureorder.application.PortsIn;
import com.secureorder.application.PortsOut;
import com.secureorder.adapter.out.security.SecurityAdapter;
import com.secureorder.adapter.out.persistence.PersistenceAdapter;
import com.secureorder.adapter.out.persistence.UserRepository;
import com.secureorder.adapter.out.persistence.OrderRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * Security configuration for the application.
 * This belongs to the config layer.
 */
@Configuration
@EnableMethodSecurity // Enables @PreAuthorize, @PostAuthorize, etc.
public class SecurityConfig {
    
    @PostConstruct
    public void init() {
        System.out.println("DEBUG: SecurityConfig initialized");
    }

    // In a real implementation, these would be injected via constructor
    // For this skeleton, we're showing the structure
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        System.out.println("DEBUG: SecurityFilterChain configured with permitAll");
        http
            // Disable all security for debugging
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()
            )
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
        ;
        
        return http.build();
    }

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        // Set allowed origins - in production, these would be explicit origins
        config.addAllowedOrigin("*"); // TODO: Replace with explicit origins in production
        config.addAllowedMethod("*");
        config.addAllowedHeader("*");
        config.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // In a real implementation, we would use BCrypt with cost >= 12 or Argon2id
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public SecurityAdapter securityAdapter(@Value("${JWT_SECRET}") String jwtSecret, PersistenceAdapter persistenceAdapter) {
        return new SecurityAdapter(jwtSecret, persistenceAdapter);
    }

    // Adapter beans - wiring the hexagonal architecture
    @Bean
    public PersistenceAdapter persistenceAdapter(UserRepository userRepository, OrderRepository orderRepository, SecurityAdapter securityAdapter) {
        return new PersistenceAdapter(userRepository, orderRepository, securityAdapter);
    }

    @Bean
    public PortsIn portsIn(SecurityAdapter securityAdapter, PersistenceAdapter persistenceAdapter) {
        // In a real implementation, we would inject the actual adapters
        // For this skeleton, we're showing the dependency injection concept
        return new com.secureorder.application.OrderService(persistenceAdapter);
    }

    @Bean
    public PortsOut portsOut(SecurityAdapter securityAdapter, PersistenceAdapter persistenceAdapter) {
        // Return a composite adapter that delegates to both persistence and security adapters
        return new com.secureorder.adapter.out.CompositePortsOut(persistenceAdapter, securityAdapter);
    }
}