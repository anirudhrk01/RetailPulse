package com.ark.retailpulse.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Data
public class EmailConfirmationRequest {

    private String email;
    private String confirmationCode;

}
