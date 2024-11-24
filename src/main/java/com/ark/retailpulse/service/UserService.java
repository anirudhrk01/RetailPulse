package com.ark.retailpulse.service;

import com.ark.retailpulse.dto.ChangePasswordRequest;
import com.ark.retailpulse.exception.ResourceNotFoundException;
import com.ark.retailpulse.model.User;
import com.ark.retailpulse.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

//    @Autowired
    private final UserRepository userRepository;
//    @Autowired
    private final PasswordEncoder passwordEncoder;

    public User registerUser(User user){
         if(userRepository.findByEmail(user.getEmail()).isPresent()){
             throw new IllegalArgumentException("Email is already in use");
         }
         user.setPassword(passwordEncoder.encode(user.getPassword()));
         user.setRole(User.Role.USER);
         return userRepository.save(user);
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


}
