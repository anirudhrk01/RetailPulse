package com.ark.retailpulse.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;


@Data
public class SmsConfirmationRequest {

    @NotBlank(message = "Phone number cannot be blank")
    private String phoneNumber;

    @NotBlank(message = "OTP code cannot be blank")
    private String otpCode;
}