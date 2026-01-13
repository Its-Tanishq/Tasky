package com.tasky.tasky.service;

import com.tasky.tasky.model.Employee;
import com.tasky.tasky.model.RefreshToken;
import com.tasky.tasky.repo.EmployeeRepo;
import com.tasky.tasky.repo.RefreshTokenRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RefreshTokenService {

    @Autowired
    private RefreshTokenRepo refreshTokenRepo;

    @Autowired
    private EmployeeRepo employeeRepo;

    @Transactional
    public RefreshToken createRefreshToken(String email) {
        Employee employee = employeeRepo.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found with email: " + email));
        
        // Delete any existing refresh tokens for this employee to prevent accumulation
        refreshTokenRepo.deleteByEmployee(employee);
        
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setEmployee(employee);
        refreshToken.setToken(java.util.UUID.randomUUID().toString());
        return refreshTokenRepo.save(refreshToken);
    }

    public RefreshToken verifyRefreshToken(String token) {
        RefreshToken refreshToken = refreshTokenRepo.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid refresh token"));

        if (refreshToken.getExpiryDate().isBefore(java.time.LocalDateTime.now())) {
            refreshTokenRepo.delete(refreshToken);
            throw new IllegalArgumentException("Refresh token has expired");
        }

        return refreshToken;
    }
}
