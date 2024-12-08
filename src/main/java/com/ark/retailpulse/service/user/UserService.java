package com.ark.retailpulse.service.user;

import com.ark.retailpulse.dto.auth.ChangePasswordRequest;
import com.ark.retailpulse.dto.auth.EmailConfirmationRequest;
import com.ark.retailpulse.dto.auth.SmsConfirmationRequest;
import com.ark.retailpulse.exception.InvalidConfirmationCodeException;
import com.ark.retailpulse.exception.ResourceNotFoundException;
import com.ark.retailpulse.model.Otp;
import com.ark.retailpulse.model.User;
import com.ark.retailpulse.repository.OtpRepository;
import com.ark.retailpulse.repository.UserRepository;
import com.ark.retailpulse.service.emaiil.EmailService;
import com.ark.retailpulse.service.sms.TwilioOtpService;
import com.ark.retailpulse.util.CodeGeneratorUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final TwilioOtpService twilioOtpService;
    private final CodeGeneratorUtil codeGeneratorUtil;
    private final OtpRepository otpRepository;

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private static final int MAX_RESEND_COUNT = 3;
    private static final int RESEND_LIMIT_WINDOW_MINUTES = 15;

    public User registerUser(User user) {
        validateUserDoesNotExist(user.getEmail(), user.getPhoneNumber());

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(User.Role.USER);

        Otp otp = generateAndSaveOtp(user);
        emailService.sendEmailConfirmationCode(user);
        twilioOtpService.sendOtp(otp);

        return userRepository.save(user);
    }

    public void confirmEmail(EmailConfirmationRequest request) {
        Otp otp = findOtpByEmail(request.getEmail());

        validateOtp(otp.getEmailOtpCode(), request.getConfirmationCode(), otp.getEmailOtpExpirationTime());

        otp.setEmailConfirmation(true);
        otpRepository.save(otp);

        updateUserOtpVerification(otp.getUser().getId());
    }

    public void confirmPhoneNumber(SmsConfirmationRequest request) {
        Otp otp = findOtpByPhoneNumber(request.getPhoneNumber());

        validateOtp(otp.getSmsOtpCode(), request.getOtpCode(), otp.getSmsOtpExpirationTime());

        otp.setPhoneConfirmation(true);
        otpRepository.save(otp);

        updateUserOtpVerification(otp.getUser().getId());
    }


    public void changePassword(String email, ChangePasswordRequest request) {
        User user = findUserByEmail(email);

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect.");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    private void validateUserDoesNotExist(String email, String phoneNumber) {
        if (userRepository.existsByEmail(email) || userRepository.existsByPhoneNumber(phoneNumber)) {
            throw new IllegalArgumentException("User with email or phone number already exists.");
        }
    }

    private Otp generateAndSaveOtp(User user) {
        String smsCode = codeGeneratorUtil.generateConfirmationCode();
        String emailCode = codeGeneratorUtil.generateConfirmationCode();

        Otp otp = new Otp();
        otp.setPhoneNumber(user.getPhoneNumber());
        otp.setEmail(user.getEmail());
        otp.setSmsOtpCode(smsCode);
        otp.setEmailOtpCode(emailCode);
        otp.setSmsOtpExpirationTime(LocalDateTime.now().plusMinutes(5));
        otp.setEmailOtpExpirationTime(LocalDateTime.now().plusMinutes(5));
        otp.setUser(user);

        return otpRepository.save(otp);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found for the given email."));
    }

    private User findUserByPhoneNumber(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new ResourceNotFoundException("User not found for the given phone number."));
    }

    private Otp findOtpByEmail(String email) {
        return otpRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("No OTP found for the given email."));
    }

    private Otp findOtpByPhoneNumber(String phoneNumber) {
        return otpRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new ResourceNotFoundException("No OTP found for the given phone number."));
    }

    private void validateOtp(String actualCode, String providedCode, LocalDateTime expirationTime) {
        if (!actualCode.equals(providedCode) || expirationTime.isBefore(LocalDateTime.now())) {
            throw new InvalidConfirmationCodeException("Invalid or expired confirmation code.");
        }
    }

    private void updateUserOtpVerification(Long userId) {
        Otp otp = otpRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Otp not found for user ID: " + userId));

        boolean isOtpVerified = otp.isEmailConfirmation() || otp.isPhoneConfirmation();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        user.setOtpVerified(isOtpVerified);
        userRepository.save(user);
    }

    public void resendOtp(String identifier, boolean isPhoneOtp) {

        // Find the OTP by email or phone number
        Otp otp = otpRepository.findByEmail(identifier)
                .orElseThrow(() -> new ResourceNotFoundException("OTP not found for the given identifier"));

        // Rate-limiting check
        if (otp.getLastResendAttempt() != null &&
                otp.getLastResendAttempt().isAfter(LocalDateTime.now().minusMinutes(RESEND_LIMIT_WINDOW_MINUTES))) {
            if (otp.getResendCount() >= MAX_RESEND_COUNT) {
                throw new IllegalStateException("Resend limit exceeded. Please try again later.");
            }
        } else {
            // Reset the resend count if outside the rate-limiting window
            otp.setResendCount(0);
        }

        // Update the resend count and timestamp
        otp.setResendCount(otp.getResendCount() + 1);
        otp.setLastResendAttempt(LocalDateTime.now());

        // Reset expiration timestamp
        if (isPhoneOtp) {
            otp.setSmsOtpExpirationTime(LocalDateTime.now().plusMinutes(5));
        } else {
            otp.setEmailOtpExpirationTime(LocalDateTime.now().plusMinutes(5));
        }

        // Regenerate OTP (pseudo-code, implement actual logic)
        if (isPhoneOtp) {
            otp.setSmsOtpCode(codeGeneratorUtil.generateConfirmationCode());
        } else {
            otp.setEmailOtpCode(codeGeneratorUtil.generateConfirmationCode());
        }

        otpRepository.save(otp);
        User user = otp.getUser();

        // Trigger resend logic (e.g., send OTP via SMS/Email)
        if (isPhoneOtp) {
            twilioOtpService.sendOtp(otp); // Sending OTP via Twilio for phone
        } else {
            emailService.sendEmailConfirmationCode(user); // Sending OTP via email
        }
    }

}
