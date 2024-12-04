package com.ark.retailpulse.service.sms;

import com.ark.retailpulse.model.Otp;
import com.ark.retailpulse.model.User;
import com.ark.retailpulse.repository.OtpRepository;
import com.twilio.Twilio;
import com.twilio.exception.ApiException;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class TwilioOtpService {
    private static final Logger logger = LoggerFactory.getLogger(TwilioOtpService.class);
    private final OtpRepository otpRepository;

    @Value("${twilio.accountSid}")
    private String accountSid;

    @Value("${twilio.authToken}")
    private String authToken;

    @Value("${twilio.phoneNumber}")
    private String fromPhoneNumber;

    @PostConstruct
    public void init() {
        Twilio.init(accountSid, authToken);
    }

    public void sendOtp(User user) {
        if (user == null || user.getPhoneNumber() == null || !isValidPhoneNumber(user.getPhoneNumber())) {
            throw new IllegalArgumentException("Invalid user or phone number");
        }

        // Invalidate old OTPs
        Optional<Otp> existingOtp = otpRepository.findByPhoneNumber(user.getPhoneNumber());
        existingOtp.ifPresent(otpRepository::delete);

        // Generate a new OTP
        String otpCode = generateOtp();
        logger.info("Generated sms OTP: {}", otpCode);

        // Save new OTP
        Otp otp = new Otp();
        otp.setPhoneNumber(user.getPhoneNumber());
        otp.setOtpCode(otpCode);
        otp.setUser(user); // Associate OTP with user
        otp.setExpirationTime(LocalDateTime.now().plusMinutes(5));
        otpRepository.save(otp);

        // Send OTP via Twilio
        try {
//            Message.creator(
//                    new PhoneNumber(user.getPhoneNumber()),
//                    new PhoneNumber(fromPhoneNumber),
//                    "Your OTP code is: " + otpCode
//            ).create(); //todo:  uncomment for demo purpose

        } catch (ApiException ex) {
            throw new RuntimeException("Failed to send OTP via Twilio: " + ex.getMessage());
        }
    }

    public boolean validateOtp(String phoneNumber, String otpCode) {

        logger.debug("Validating OTP for phoneNumber: {}, otpCode: {}", phoneNumber, otpCode);
        Otp otp = otpRepository.findByPhoneNumberAndOtpCode(phoneNumber, otpCode)
                .orElseThrow(() -> new IllegalArgumentException("Invalid OTP"));
        logger.debug("Retrieved OTP: {}", otp);


        if (otp.getExpirationTime().isBefore(LocalDateTime.now())) {
            logger.debug("OTP expired for phoneNumber: {}, otpCode: {}", phoneNumber, otpCode);
            otpRepository.delete(otp); // Cleanup expired OTP
            throw new IllegalArgumentException("OTP has expired");
        }

        otpRepository.delete(otp); // OTP is validated, so remove it
        return true;
    }

    private String generateOtp() {
        Random random = new Random();
        return String.valueOf(100000 + random.nextInt(900000));
    }
    private boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber.matches("\\+?[0-9]{10,15}");
    }

}
