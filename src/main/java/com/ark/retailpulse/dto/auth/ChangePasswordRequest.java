package com.ark.retailpulse.dto.auth;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

/**
 * DTO for handling change password requests.
 */
@Data
public class ChangePasswordRequest {


    @NotEmpty(message = "Current password is required")
    private String currentPassword;

    @NotEmpty(message = "New password is required")
    private String newPassword;


}