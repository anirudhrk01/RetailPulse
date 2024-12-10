package com.ark.retailpulse.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
/**
 * Represents an OTP (One-Time Password) entity for email and phone verification.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Otp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String phoneNumber;
    private String email;


    private String smsOtpCode;
    private String emailOtpCode;

    private boolean emailConfirmation = false;
    private boolean phoneConfirmation = false;

    private LocalDateTime smsOtpExpirationTime ;
    private LocalDateTime emailOtpExpirationTime;

    private int resendCount = 0;
    private LocalDateTime lastResendAttempt;
    /**
     * The user associated with this OTP record.
     */
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
