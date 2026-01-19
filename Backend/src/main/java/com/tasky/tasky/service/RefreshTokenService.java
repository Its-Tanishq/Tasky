package com.tasky.tasky.service;

import com.tasky.tasky.model.Employee;
import com.tasky.tasky.model.RefreshToken;
import com.tasky.tasky.repo.EmployeeRepo;
import com.tasky.tasky.repo.RefreshTokenRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class RefreshTokenService {

    @Autowired
    private RefreshTokenRepo refreshTokenRepo;

    @Autowired
    private EmployeeRepo employeeRepo;

    @Transactional
    public RefreshToken createRefreshToken(String email, String token) {
        Employee employee = employeeRepo.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found with email: " + email));
        
        refreshTokenRepo.deleteByEmployee(employee);
        
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setEmployee(employee);
        refreshToken.setToken(token);
        return refreshTokenRepo.save(refreshToken);
    }

    @Transactional
    public boolean isValidRefreshToken(String token) {
        Optional<RefreshToken> refreshTokenOpt = refreshTokenRepo.findByToken(token);
        
        if (refreshTokenOpt.isEmpty()) {
            return false; 
        }
        
        RefreshToken refreshToken = refreshTokenOpt.get();
        
        if (refreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            refreshTokenRepo.delete(refreshToken);
            return false;
        }
        
        return true; 
    }

    @Transactional
    public void removeToken(String email) {
        Employee employee = employeeRepo.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found with email: " + email));

        refreshTokenRepo.deleteByEmployee(employee);
    }
}
