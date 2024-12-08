package com.ark.retailpulse.repository;

import com.ark.retailpulse.model.Otp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface OtpRepository extends JpaRepository<Otp, Long> {

    Optional<Otp> findByPhoneNumber(String phoneNumber);


    void deleteAllBySmsOtpExpirationTimeBefore(LocalDateTime time);
    void deleteAllByEmailOtpExpirationTimeBefore(LocalDateTime time);

    boolean existsByUserIdAndEmailConfirmationTrueOrPhoneConfirmationTrue(Long id);

    Optional<Otp> findByUserId(Long id);
    Optional<Otp> findByEmail(String email);


    Optional<Otp> findByEmailAndEmailOtpCode(String email, String confirmationCode);

    Optional<Otp> findByPhoneNumberAndSmsOtpCode(String phoneNumber, String otpCode);

}
