package com.ark.retailpulse.repository;

import com.ark.retailpulse.model.Otp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface OtpRepository extends JpaRepository<Otp, Long> {

    Optional<Otp> findByPhoneNumber(String phoneNumber);

    Optional<Otp> findByPhoneNumberAndOtpCode(String phoneNumber, String otpCode);

    void deleteAllByExpirationTimeBefore(LocalDateTime now);

    boolean existsByUserIdAndEmailConfirmationTrueOrPhoneConfirmationTrue(Long id);

}
