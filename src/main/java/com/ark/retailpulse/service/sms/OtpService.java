//package com.ark.retailpulse.service.sms;
//
//import com.ark.retailpulse.exception.InvalidOtpException;
//import com.ark.retailpulse.exception.OtpResendLimitExceededException;
//import com.ark.retailpulse.exception.ResourceNotFoundException;
//import com.ark.retailpulse.model.Otp;
//import com.ark.retailpulse.model.User;
//import com.ark.retailpulse.repository.OtpRepository;
//import com.ark.retailpulse.repository.UserRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDateTime;
//
//@Service
//@RequiredArgsConstructor
//public class OtpService {
//    private final OtpRepository otpRepository;
////
//    private static final int MAX_RESEND_COUNT = 3;
//    private static final int RESEND_LIMIT_WINDOW_MINUTES = 15;
//
//    public void resendOtp(String identifier, boolean isPhoneOtp) {
//        Otp otp;
//        if (isPhoneOtp) {
//            otp = otpRepository.findByPhoneNumber(identifier)
//                    .orElseThrow(() -> new ResourceNotFoundException("OTP not found for the given phone number"));
//        } else {
//            otp = otpRepository.findByEmail(identifier)
//                    .orElseThrow(() -> new ResourceNotFoundException("OTP not found for the given email"));
//        }
//
//        // Rate-limiting check
//        if (otp.getLastResendAttempt() != null && otp.getLastResendAttempt().isAfter(LocalDateTime.now().minusMinutes(RESEND_LIMIT_WINDOW_MINUTES))) {
//            if (otp.getResendCount() >= MAX_RESEND_COUNT) {
//                throw new OtpResendLimitExceededException("Resend limit exceeded. Please try again later.");
//            }
//        } else {
//            // Reset the resend count if outside the rate-limiting window
//            otp.setResendCount(0);
//        }
//
//        // Update the resend count and timestamp
//        otp.setResendCount(otp.getResendCount() + 1);
//        otp.setLastResendAttempt(LocalDateTime.now());
//
//        // Reset expiration timestamp
//        if (isPhoneOtp) {
//            otp.setSmsOtpExpirationTime(LocalDateTime.now().plusMinutes(5));
//        } else {
//            otp.setEmailOtpExpirationTime(LocalDateTime.now().plusMinutes(5));
//        }
//
//        // Regenerate OTP
//        if (isPhoneOtp) {
//            otp.setSmsOtpCode(generateOtp());
//        } else {
//            otp.setEmailOtpCode(generateOtp());
//        }
//
//        otpRepository.save(otp);
//
//        // Trigger resend logic (e.g., send OTP via SMS/Email)
//        if (isPhoneOtp) {
//            sendSmsOtp(otp.getPhoneNumber(), otp.getSmsOtpCode());
//        } else {
//            sendEmailOtp(otp.getEmail(), otp.getEmailOtpCode());
//        }
//    }
//
//    private String generateOtp() {
//        return String.valueOf((int) (Math.random() * 9000) + 1000); // Generates a 4-digit OTP
//    }
//
//    private void sendSmsOtp(String phoneNumber, String otpCode) {
//        // Implement SMS sending logic with Twilio or other service
//    }
//
//    private void sendEmailOtp(String email, String otpCode) {
//        // Implement email sending logic with an email provider
//    }
//}
