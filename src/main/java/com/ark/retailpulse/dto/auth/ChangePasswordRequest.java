package com.ark.retailpulse.dto.auth;

import lombok.Data;

@Data
public class ChangePasswordRequest {


    private String currentPassword;
    private String newPassword;



}