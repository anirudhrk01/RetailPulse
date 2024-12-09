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
    @Scheduled(fixedRate = 60000)
    public void cleanupExpiredOtps() {
        LocalDateTime now = LocalDateTime.now();
        otpRepository.deleteAllBySmsOtpExpirationTimeBefore(now);
        otpRepository.deleteAllByEmailOtpExpirationTimeBefore(now);
    }
}
