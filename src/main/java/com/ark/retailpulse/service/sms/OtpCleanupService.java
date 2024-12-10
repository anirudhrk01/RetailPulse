package com.ark.retailpulse.service.sms;

import com.ark.retailpulse.repository.OtpRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OtpCleanupService {

    private final OtpRepository otpRepository;

    /**
     * Cleans up expired OTPs (both SMS and email) every 1 minute.
     */
    @Transactional
    @Scheduled(fixedRate = 60000) // Runs every 1 minute (60000 milliseconds)
    public void cleanupExpiredOtps() {
        LocalDateTime now = LocalDateTime.now();

        // Delete expired SMS OTPs
        otpRepository.deleteAllBySmsOtpExpirationTimeBefore(now);

        // Delete expired email OTPs
        otpRepository.deleteAllByEmailOtpExpirationTimeBefore(now);
    }
}
