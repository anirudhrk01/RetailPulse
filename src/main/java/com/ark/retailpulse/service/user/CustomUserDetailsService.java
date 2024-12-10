package com.ark.retailpulse.service.user;


import com.ark.retailpulse.model.User;
import com.ark.retailpulse.repository.OtpRepository;
import com.ark.retailpulse.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
/**
 * Service class responsible for loading user details for authentication purposes.
 * Implements the Spring Security UserDetailsService interface to retrieve user information
 * based on their username (email in this case).
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {


    private final UserRepository userRepository;
    private final OtpRepository otpRepository;

    /**
     * Loads the user details based on the provided username (email).
     * Verifies if the user has confirmed their email or phone number through OTP confirmation.
     *
     * @param username the email of the user to be loaded
     * @return UserDetails containing the user's information and OTP verification status
     * @throws UsernameNotFoundException if the user is not found
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Retrieve the user by email
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Check if the user has either email or phone confirmation
        boolean isEnabled = otpRepository.existsByUserIdAndEmailConfirmationTrueOrPhoneConfirmationTrue(user.getId());
        user.setOtpVerified(isEnabled);

        return user;
    }

}
