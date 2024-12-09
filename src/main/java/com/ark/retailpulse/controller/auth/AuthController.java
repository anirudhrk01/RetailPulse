package com.ark.retailpulse.controller.auth;

import com.ark.retailpulse.dto.ApiResponse;
import com.ark.retailpulse.dto.auth.ChangePasswordRequest;
import com.ark.retailpulse.dto.auth.EmailConfirmationRequest;
import com.ark.retailpulse.dto.auth.LoginRequest;
import com.ark.retailpulse.dto.auth.SmsConfirmationRequest;
import com.ark.retailpulse.model.User;
import com.ark.retailpulse.service.auth.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);


    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody User user) {
        User registeredUser = authService.register(user);
        logger.info("User registered successfully with ID: {}", registeredUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(registeredUser);
    }

    @PostMapping("/confirm-email")
    public ResponseEntity<?> confirmEmail(@Valid @RequestBody EmailConfirmationRequest request) {
        logger.info("Received email confirmation request for email: {}", request.getEmail());
        authService.confirmEmail(request);
        logger.info("Email confirmed successfully for email: {}", request.getEmail());
        return ResponseEntity.ok(new ApiResponse("Email confirmed", HttpStatus.OK.value()));
    }

    @PostMapping("/confirm-phone")
    public ResponseEntity<?> confirmPhone(@Valid @RequestBody SmsConfirmationRequest request) {  //todo : SmsConfirmationRequest as above
        logger.info("Received phone confirmation request for phone: {}", request.getPhoneNumber());
        authService.confirmPhone(request);
        logger.info("Phone number confirmed successfully for phone: {}", request.getPhoneNumber());
        return ResponseEntity.ok(new ApiResponse("Phone number confirmed", HttpStatus.OK.value()));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletRequest request, HttpServletResponse response) {
        logger.info("Login attempt for user: {}", loginRequest.getEmail());
        authService.login(loginRequest, request, response);
        logger.info("Login successful for user: {}", loginRequest.getEmail());
        return ResponseEntity.ok(new ApiResponse("Login successful", HttpStatus.OK.value()));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        logger.info("Logout request received.");
        authService.logout(request);
        logger.info("Logout successful.");
        return ResponseEntity.ok(new ApiResponse("Logout successful", HttpStatus.OK.value()));
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequest changePasswordRequest) {
        logger.info("Password change request received for user");
        authService.changePassword(changePasswordRequest);
        logger.info("Password changed successfully for user");
        return ResponseEntity.ok(new ApiResponse("Password changed successfully", HttpStatus.OK.value()));
    }




}
