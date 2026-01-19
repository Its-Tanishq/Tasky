package com.tasky.tasky.controller;

import com.tasky.tasky.dto.EmployeeDTO;
import com.tasky.tasky.security.JWTUtil;
import com.tasky.tasky.service.EmployeeService;
import com.tasky.tasky.service.RefreshTokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @PostMapping("/create")
    public ResponseEntity<?> createEmployee(@Valid @RequestBody EmployeeDTO employeeDTO, HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        String token = authHeader.substring(7); // Remove "Bearer " prefix
        int empId = jwtUtil.getEmpIdFromToken(token);
        employeeService.createEmployee(employeeDTO, empId);
        return ResponseEntity.ok().body("Employee created successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginEmployee(@RequestBody EmployeeDTO employeeDTO) {
        employeeService.loginEmployee(employeeDTO);

        String accessToken = jwtUtil.generateAccessToken(employeeDTO.getEmail());
        String refreshToken = jwtUtil.generateRefreshToken(employeeDTO.getEmail());

        refreshTokenService.createRefreshToken(employeeDTO.getEmail());

        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(false) // Set to true in production
                .path("/")
                .maxAge(7 * 24 * 60 * 60) // 7 days in seconds
                .build();

        Map<String, String> response = Map.of(
                "message", "Employee Logged In Successfully",
                "accessToken", accessToken
        );

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(response);
    }

    @PutMapping("/update/{employeeId}")
    public ResponseEntity<?> updateEmployee(@PathVariable Long employeeId, @Valid @RequestBody EmployeeDTO employeeDTO) {
        employeeService.updateEmployee(employeeId, employeeDTO);
        return ResponseEntity.ok().body("Employee updated successfully");
    }

    @PutMapping("/update-password/{employeeId}")
    public ResponseEntity<?> updateEmployeePassword(@PathVariable Long employeeId, @RequestBody String newPassword) {
        employeeService.updatePassword(employeeId, newPassword);
        return ResponseEntity.ok().body("Employee password updated successfully");
    }

    @DeleteMapping("/delete/{employeeId}")
    public ResponseEntity<?> deleteEmployee(@PathVariable Long employeeId) {
        employeeService.deleteEmployee(employeeId);
        return ResponseEntity.ok().body("Employee deleted successfully");
    }
}
