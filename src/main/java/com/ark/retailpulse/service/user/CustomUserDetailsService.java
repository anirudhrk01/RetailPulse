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

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final OtpRepository otpRepository;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));


        boolean isEnabled = otpRepository.existsByUserIdAndEmailConfirmationTrueOrPhoneConfirmationTrue(user.getId());
        user.setEnabled(isEnabled);

        return user;
    }

}
