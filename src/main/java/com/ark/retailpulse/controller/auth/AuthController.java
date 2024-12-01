package com.ark.retailpulse.controller.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ark.retailpulse.dto.auth.ChangePasswordRequest;
import com.ark.retailpulse.dto.auth.EmailConfirmationRequest;
import com.ark.retailpulse.dto.auth.LoginRequest;
import com.ark.retailpulse.exception.ResourceNotFoundException;
import com.ark.retailpulse.service.user.UserService;
import com.ark.retailpulse.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final AuthenticationManager authenticationManager;
    private final UserService userService;


    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest,HttpServletRequest request) {
        try {
            // Authenticate user credentials
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );

            // Set the authentication in the SecurityContext
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Create a session if one doesn't exist and log the session ID
            HttpSession session = request.getSession(true);  // true will create the session if it doesn't exist
            logger.info("Session ID: " + session.getId());  // Log the session ID to track its creation

            // Return success response
            return ResponseEntity.ok("Login successful");
        } catch (BadCredentialsException e) {
            return ResponseEntity.badRequest().body("Invalid email or password");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@Valid @RequestBody User user){
        return ResponseEntity.ok(userService.registerUser(user));
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequest request ){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        userService.changePassword(email ,request);
        return ResponseEntity.ok().body("password changed");
    }

    @PostMapping("/confirm-email")
    public ResponseEntity<?> confirmEmail(@RequestBody EmailConfirmationRequest request){
        try{
                userService.confirmEmail(request.getEmail(), request.getConfirmationCode());
                return ResponseEntity.ok().body("Email confirmed successfully");
        }catch (BadCredentialsException e){
            return ResponseEntity.badRequest().body("Invalid confirmation code .");
        }
        catch (ResourceNotFoundException e){
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate(); // Invalidate session
        }
        return ResponseEntity.ok("Logged out successfully");
    }


}
