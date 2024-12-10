package com.ark.retailpulse.service.auth;

import com.ark.retailpulse.dto.auth.ChangePasswordRequest;
import com.ark.retailpulse.dto.auth.EmailConfirmationRequest;
import com.ark.retailpulse.dto.auth.LoginRequest;
import com.ark.retailpulse.dto.auth.SmsConfirmationRequest;
import com.ark.retailpulse.exception.ResourceNotFoundException;
import com.ark.retailpulse.exception.UnverifiedOtpException;
import com.ark.retailpulse.service.user.CustomUserDetailsService;
import com.ark.retailpulse.service.user.UserService;
import com.ark.retailpulse.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();
    private final PasswordEncoder passwordEncoder;


    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    /**
     * Registers a new user.
     *
     * @param user User details to be registered.
     * @return Registered user.
     */
    public User register(User user) {
        logger.info("Attempting to register user with email: {}", user.getEmail());
        User registeredUser = userService.registerUser(user);
        logger.info("User registered successfully with ID: {}", registeredUser.getId());
        return registeredUser;
    }

    /**
     * Confirms the user's email using a confirmation request.
     *
     * @param request Email confirmation request containing email and confirmation code.
     */
    public void confirmEmail(EmailConfirmationRequest request) {
        logger.info("Confirming email for: {}", request.getEmail());
        userService.confirmEmail(request);
        logger.info("Email confirmed successfully for: {}", request.getEmail());
    }

    /**
     * Confirms the user's phone number using an SMS confirmation request.
     *
     * @param request SMS confirmation request containing phone number and OTP.
     */
    public void confirmPhone(SmsConfirmationRequest request) {
        logger.info("Confirming phone number: {}", request.getPhoneNumber());
        userService.confirmPhoneNumber(request);
        logger.info("Phone number confirmed successfully for: {}", request.getPhoneNumber());
    }

    /**
     * Logs in a user by validating credentials and setting up the security context.
     *
     * @param loginRequest Contains the user's email and password.
     * @param request      HTTP servlet request.
     * @param response     HTTP servlet response.
     */
    public void login(LoginRequest loginRequest, HttpServletRequest request, HttpServletResponse response) {
        logger.info("Login attempt for user: {}", loginRequest.getEmail());

        // Find the user by email
        User user = userService.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> {
                    logger.error("User not found with email: {}", loginRequest.getEmail());
                    return new ResourceNotFoundException("User not found with email: " + loginRequest.getEmail());
                });

        // Validate password
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            logger.error("Invalid password for user: {}", loginRequest.getEmail());
            throw new BadCredentialsException("Invalid password");
        }

        // Ensure OTP verification
        if (!user.isOtpVerified()) {
            logger.error("OTP not verified for user: {}", loginRequest.getEmail());
            throw new UnverifiedOtpException("User must verify OTP before logging in.");
        }

        // Authenticate user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        // Set security context
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        securityContextRepository.saveContext(context, request, response);

        logger.info("User logged in successfully: {}", loginRequest.getEmail());
    }

    /**
     * Logs out a user by invalidating their session.
     *
     * @param request HTTP servlet request.
     */
    public void logout(HttpServletRequest request) {
        logger.info("Logout request received.");
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
            logger.info("Session invalidated successfully.");
        } else {
            logger.warn("No session found to invalidate.");
        }
    }

    /**
     * Changes the user's password.
     *
     * @param request Contains the current and new password details.
     */
    public void changePassword(ChangePasswordRequest request) {
        logger.info("Password change request received ");

        // Retrieve authenticated user's email
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        // Delegate password change to UserService
        userService.changePassword(email, request);
        logger.info("Password changed successfully for email: {}", email);
    }
}
