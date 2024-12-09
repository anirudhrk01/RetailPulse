package com.ark.retailpulse.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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

    private int resendCount = 0; // Resend attempts counter
    private LocalDateTime lastResendAttempt; // Timestamp for last resend attempt

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
