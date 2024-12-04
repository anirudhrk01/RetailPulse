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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody User user) {
        User registeredUser = authService.register(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(registeredUser);
    }

    @PostMapping("/confirm-email")
    public ResponseEntity<?> confirmEmail(@Valid @RequestBody EmailConfirmationRequest request) {
        authService.confirmEmail(request);
        return ResponseEntity.ok(new ApiResponse("Email confirmed", HttpStatus.OK.value()));
    }

    @PostMapping("/confirm-phone")
    public ResponseEntity<?> confirmPhone(@Valid @RequestParam SmsConfirmationRequest request) {  //todo : SmsConfirmationRequest as above
        authService.confirmPhone(request);
        return ResponseEntity.ok(new ApiResponse("Phone number confirmed", HttpStatus.OK.value()));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletRequest request, HttpServletResponse response) {
        authService.login(loginRequest, request, response);
        return ResponseEntity.ok(new ApiResponse("Login successful", HttpStatus.OK.value()));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        authService.logout(request);
        return ResponseEntity.ok(new ApiResponse("Logout successful", HttpStatus.OK.value()));
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequest changePasswordRequest) {
        authService.changePassword(changePasswordRequest);
        return ResponseEntity.ok(new ApiResponse("Password changed successfully", HttpStatus.OK.value()));
    }



}
