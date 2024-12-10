package com.ark.retailpulse.dto.auth;

import lombok.Data;
/**
 * DTO for handling email confirmation requests.
 */
@Data
public class EmailConfirmationRequest {

    private String email;
    private String confirmationCode;

}
