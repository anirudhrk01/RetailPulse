package com.ark.retailpulse.repository;

import com.ark.retailpulse.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByPhoneNumber(String phoneNumber);
//    void deleteByOtpVerifiedFalseAndCreatedAtBefore(LocalDateTime timestamp);


    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);


}
