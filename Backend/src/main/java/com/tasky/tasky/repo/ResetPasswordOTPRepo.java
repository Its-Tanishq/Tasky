package com.tasky.tasky.repo;

import com.tasky.tasky.model.ResetPasswordOTP;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResetPasswordOTPRepo extends JpaRepository<ResetPasswordOTP, Long> {

    Optional<ResetPasswordOTP> findByOTP(int OTP);
    @Query("SELECT r FROM ResetPasswordOTP r WHERE r.email = :email ORDER BY r.createdAt DESC LIMIT 1")
    Optional<ResetPasswordOTP> findByEmail(String email);
}
