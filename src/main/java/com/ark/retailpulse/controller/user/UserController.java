package com.ark.retailpulse.controller.user;

import com.ark.retailpulse.dto.ApiResponse;
import com.ark.retailpulse.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;


    @PostMapping("/resend-otp")
    public ResponseEntity<?> resendOtp(@RequestParam String identifier, @RequestParam boolean isPhoneOtp) {
        userService.resendOtp(identifier, isPhoneOtp);
        return ResponseEntity.ok(new ApiResponse("OTP resent successfully", HttpStatus.OK.value()));
    }

}
