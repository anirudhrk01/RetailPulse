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

/**
 * Service class responsible for user registration, password management,
 * OTP confirmation, and email/phone number verification.
 */
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

    /**
     * Registers a new user by validating uniqueness of email and phone number,
     * encoding the password, generating OTP, and sending confirmation emails/SMS.
     *
     * @param user the user to be registered
     * @return the saved user
     */
    public User registerUser(User user) {
        validateUserDoesNotExist(user.getEmail(), user.getPhoneNumber());

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(User.Role.USER);

        Otp otp = generateAndSaveOtp(user);
        emailService.sendEmailConfirmationCode(user);
        twilioOtpService.sendOtp(otp);

        return userRepository.save(user);
    }

    /**
     * Confirms the email of the user by validating the OTP confirmation code.
     *
     * @param request the request containing email and confirmation code
     */
    public void confirmEmail(EmailConfirmationRequest request) {
        Otp otp = findOtpByEmail(request.getEmail());

        validateOtp(otp.getEmailOtpCode(), request.getConfirmationCode(), otp.getEmailOtpExpirationTime());

        otp.setEmailConfirmation(true);
        otpRepository.save(otp);

        updateUserOtpVerification(otp.getUser().getId());
    }

    /**
     * Confirms the phone number of the user by validating the OTP confirmation code.
     *
     * @param request the request containing phone number and OTP code
     */
    public void confirmPhoneNumber(SmsConfirmationRequest request) {
        Otp otp = findOtpByPhoneNumber(request.getPhoneNumber());

        validateOtp(otp.getSmsOtpCode(), request.getOtpCode(), otp.getSmsOtpExpirationTime());

        otp.setPhoneConfirmation(true);
        otpRepository.save(otp);

        updateUserOtpVerification(otp.getUser().getId());
    }

    /**
     * Changes the user's password after validating the current password.
     *
     * @param email  the email of the user whose password needs to be changed
     * @param request the request containing the current and new password
     */
    public void changePassword(String email, ChangePasswordRequest request) {
        User user = findUserByEmail(email);

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect.");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    /**
     * Validates if the user with the given email or phone number already exists.
     *
     * @param email the email of the user
     * @param phoneNumber the phone number of the user
     */
    private void validateUserDoesNotExist(String email, String phoneNumber) {
        if (userRepository.existsByEmail(email) || userRepository.existsByPhoneNumber(phoneNumber)) {
            throw new IllegalArgumentException("User with email or phone number already exists.");
        }
    }

    /**
     * Generates and saves OTP codes for email and phone number confirmation.
     *
     * @param user the user for whom OTP is being generated
     * @return the saved OTP entity
     */
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

    /**
     * Finds a user by their email address.
     *
     * @param email the email of the user
     * @return an Optional containing the user if found, otherwise empty
     */
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Finds a user by their email address and throws an exception if not found.
     *
     * @param email the email of the user
     * @return the user
     * @throws ResourceNotFoundException if the user is not found
     */
    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found for the given email."));
    }

    /**
     * Finds a user by their phone number and throws an exception if not found.
     *
     * @param phoneNumber the phone number of the user
     * @return the user
     * @throws ResourceNotFoundException if the user is not found
     */
    private User findUserByPhoneNumber(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new ResourceNotFoundException("User not found for the given phone number."));
    }

    /**
     * Finds an OTP record by email address.
     *
     * @param email the email of the user
     * @return the OTP record
     * @throws ResourceNotFoundException if OTP is not found
     */
    private Otp findOtpByEmail(String email) {
        return otpRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("No OTP found for the given email."));
    }

    /**
     * Finds an OTP record by phone number.
     *
     * @param phoneNumber the phone number of the user
     * @return the OTP record
     * @throws ResourceNotFoundException if OTP is not found
     */
    private Otp findOtpByPhoneNumber(String phoneNumber) {
        return otpRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new ResourceNotFoundException("No OTP found for the given phone number."));
    }

    /**
     * Validates the provided OTP against the stored OTP and checks expiration time.
     *
     * @param actualCode the actual OTP code
     * @param providedCode the provided OTP code
     * @param expirationTime the expiration time of the OTP
     * @throws InvalidConfirmationCodeException if the code is invalid or expired
     */
    private void validateOtp(String actualCode, String providedCode, LocalDateTime expirationTime) {
        if (!actualCode.equals(providedCode) || expirationTime.isBefore(LocalDateTime.now())) {
            throw new InvalidConfirmationCodeException("Invalid or expired confirmation code.");
        }
    }

    /**
     * Updates the user's OTP verification status after email or phone number confirmation.
     *
     * @param userId the ID of the user
     */
    private void updateUserOtpVerification(Long userId) {
        Otp otp = otpRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Otp not found for user ID: " + userId));

        boolean isOtpVerified = otp.isEmailConfirmation() || otp.isPhoneConfirmation();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        user.setOtpVerified(isOtpVerified);
        userRepository.save(user);
    }

    /**
     * Resends the OTP to the user via either SMS or email, implementing rate-limiting.
     *
     * @param identifier the email or phone number to identify the user
     * @param isPhoneOtp true if the OTP is for phone number, false for email
     * @throws IllegalStateException if the resend limit is exceeded
     */
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
