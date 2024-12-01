package com.ark.retailpulse.dto.auth;

import lombok.Data;

@Data
public class EmailConfirmationRequest {

    private String email;
    private String confirmationCode;

}
