package com.ark.retailpulse.service.auth;

import com.ark.retailpulse.dto.auth.ChangePasswordRequest;
import com.ark.retailpulse.dto.auth.EmailConfirmationRequest;
import com.ark.retailpulse.dto.auth.LoginRequest;
import com.ark.retailpulse.dto.auth.SmsConfirmationRequest;
import com.ark.retailpulse.exception.ResourceNotFoundException;
import com.ark.retailpulse.exception.UnverifiedOtpException;
import com.ark.retailpulse.service.sms.TwilioOtpService;
import com.ark.retailpulse.service.user.CustomUserDetailsService;
import com.ark.retailpulse.service.user.UserService;
import com.ark.retailpulse.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
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
    private final TwilioOtpService twilioOtpService;  // TODO: Implement OTP-related functionality
    private final SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();
    private final CustomUserDetailsService customUserDetailsService;
    private final PasswordEncoder passwordEncoder;


    public User register(User user) {
        return userService.registerUser(user);
    }

    public void confirmEmail(EmailConfirmationRequest request) {
        userService.confirmEmail(request);
    }

    public void confirmPhone(SmsConfirmationRequest request) {
        userService.confirmPhoneNumber(request);
    }

    public void login(LoginRequest loginRequest, HttpServletRequest request, HttpServletResponse response) {

        User user = userService.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + loginRequest.getEmail()));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid password");
        }

        if (!user.isOtpVerified()) {
            throw new UnverifiedOtpException("User must verify OTP before logging in.");
        }

        // Authenticate and set the Security Context
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        securityContextRepository.saveContext(context, request, response);

//        HttpSession session = request.getSession(true);
//        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);
    }

    public void logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }

    public void changePassword(ChangePasswordRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        userService.changePassword(email, request);
    }




}
