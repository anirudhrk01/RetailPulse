package com.ark.retailpulse.config;

import com.ark.retailpulse.service.user.CustomUserDetailsService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class); // Logger instance

    private final CustomUserDetailsService customUserDetailsService; // Inject custom user details service

    /**
     * Bean to define the password encoder.
     * BCryptPasswordEncoder is a secure choice for encoding passwords.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        logger.info("Initializing BCryptPasswordEncoder...");
        return new BCryptPasswordEncoder();
    }

    /**
     * Bean to configure the AuthenticationManager.
     * This manager handles authentication for the application.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        logger.info("Initializing AuthenticationManager...");
        return authConfig.getAuthenticationManager();
    }

    /**
     * Bean to configure the DaoAuthenticationProvider.
     * Connects the CustomUserDetailsService with the password encoder.
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        logger.info("Configuring DaoAuthenticationProvider...");
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(customUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Bean to define the security filter chain.
     * Configures authentication, authorization, exception handling, logout, and session management.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        logger.info("Building SecurityFilterChain...");
        return http
                // Disable CSRF protection for stateless API security
                .csrf(AbstractHttpConfigurer::disable)
                // Configure authorization rules for endpoints
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers(
                                "/api/auth/**",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/",
                                "/index.html",
                                "/images/**",
                                "/api/verify-payment"
                        ).permitAll() // Publicly accessible endpoints
                        .requestMatchers("/api/user/resend-otp").permitAll()
                        .requestMatchers("/api/users/**", "/api/cart/**", "/api/products/**", "/api/orders/**", "/api/payment/**")
                        .hasAnyRole("USER", "ADMIN") // Role-based access for specific endpoints
                        .anyRequest().authenticated() // All other endpoints require authentication
                )
                // Configure exception handling for authentication and access denial
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) -> {
                            logger.warn("Unauthorized access attempt: {}", authException.getMessage());
                            response.setContentType("application/json");
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.getWriter().write("{\"error\": \"Unauthorized access: You need to login to access this resource\"}");
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            logger.error("Access denied: {}", accessDeniedException.getMessage());
                            response.setContentType("application/json");
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.getWriter().write("{\"error\": \"You do not have permission to access this resource\"}");
                        })
                )
                // Configure logout behavior
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/api/auth/logout"))
                        .invalidateHttpSession(true) // Invalidate session on logout
                        .deleteCookies("SESSION") // Delete session cookies
                        .logoutSuccessHandler((request, response, authentication) -> {
                            logger.info("User logged out successfully.");
                            response.setContentType("application/json");
                            response.setStatus(HttpServletResponse.SC_OK);
                            response.getWriter().write("{\"message\": \"Logged out successfully\"}");
                        })
                )
                // Configure session management policy
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED) // Create session only when required
                )
                .build();
    }
}
