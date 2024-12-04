package com.ark.retailpulse.service.user;

import com.ark.retailpulse.dto.auth.ChangePasswordRequest;
import com.ark.retailpulse.exception.InvalidConfirmationCodeException;
import com.ark.retailpulse.exception.ResourceNotFoundException;
import com.ark.retailpulse.model.User;
import com.ark.retailpulse.repository.UserRepository;
import com.ark.retailpulse.service.emaiil.EmailService;
import com.ark.retailpulse.service.sms.OtpCleanupService;
import com.ark.retailpulse.service.sms.TwilioOtpService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final TwilioOtpService twilioOtpService;
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public User registerUser(User user){
         if(userRepository.findByEmail(user.getEmail()).isPresent()){
             throw new IllegalArgumentException("Email is already in use");
         }

        if (userRepository.findByPhoneNumber(user.getPhoneNumber()).isPresent()) {
            throw new IllegalArgumentException("Phone number is already in use");
        }

         user.setPassword(passwordEncoder.encode(user.getPassword()));
         user.setRole(User.Role.USER);
         user.setConfirmationCode(generateConfirmationCode());
         user.setEmailConfirmation(false);
         user.setPhoneConfirmation(false);

         emailService.sendConfirmationCode(user);

         User savedUser = userRepository.save(user);
         twilioOtpService.sendOtp(savedUser);

         return savedUser;
    }

    public User getUserByEmail(String email){
            return userRepository.findByEmail(email).orElseThrow(()-> new ResourceNotFoundException("User not found"));
    }

    public void changePassword(String email, ChangePasswordRequest request){
              User user = getUserByEmail(email);

              if(!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())){
                  throw new BadCredentialsException("current password is incorrect");
                }

              user.setPassword(passwordEncoder.encode(request.getNewPassword()));
              userRepository.save(user);
    }

    public void confirmEmail(String email, String confirmationCode){
            User user = getUserByEmail(email);

            if(user.getConfirmationCode().equals(confirmationCode)){
                user.setEmailConfirmation(true);
                user.setConfirmationCode(null);
                userRepository.save(user);
            }
            else{
                throw new InvalidConfirmationCodeException("confirmation code is incorrect");
            }
    }

    private String generateConfirmationCode(){
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }

    public void confirmPhoneNumber(String phoneNumber, String otpCode) {
        boolean isValid = twilioOtpService.validateOtp(phoneNumber, otpCode);
        if (isValid) {
            logger.debug("Finding user by phoneNumber: {}", phoneNumber);
            User user = userRepository.findByPhoneNumber(phoneNumber)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));
            user.setPhoneConfirmation(true);
            userRepository.save(user);
        }
    }



}























