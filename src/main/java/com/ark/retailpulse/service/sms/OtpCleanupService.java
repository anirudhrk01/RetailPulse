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
    @Transactional
    @Scheduled(fixedRate = 60000) // Run every 1 minute
    public void cleanupExpiredOtps() {
        otpRepository.deleteAllByExpirationTimeBefore(LocalDateTime.now());
    }
}
