package com.tasky.tasky.controller;

import com.tasky.tasky.dto.EmployeeDTO;
import com.tasky.tasky.dto.UserContextDTO;
import com.tasky.tasky.security.JWTUtil;
import com.tasky.tasky.service.EmployeeService;
import com.tasky.tasky.service.RefreshTokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
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
    public ResponseEntity<?> createEmployee(@Valid @RequestBody EmployeeDTO employeeDTO, @AuthenticationPrincipal UserContextDTO userContextDTO) {
        long empId = userContextDTO.getEmpId();
        long roleId = userContextDTO.getRoleId();
        employeeService.createEmployee(roleId, employeeDTO, empId);
        return ResponseEntity.ok().body("Employee created successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginEmployee(@RequestBody EmployeeDTO employeeDTO) {
        employeeService.loginEmployee(employeeDTO);

        String accessToken = jwtUtil.generateAccessToken(employeeDTO.getEmail());
        String refreshToken = jwtUtil.generateRefreshToken(employeeDTO.getEmail());

        refreshTokenService.createRefreshToken(employeeDTO.getEmail(), refreshToken);

        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(false) // set true on https / production
                .path("/")
                .maxAge(7 * 24 * 60 * 60)
                .sameSite("Lax") // set to None on production with https
                .build();

        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("message", "Login successful");
        responseBody.put("accessToken", accessToken);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(responseBody);
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateEmployee(@Valid @RequestBody EmployeeDTO employeeDTO, @AuthenticationPrincipal UserContextDTO userContextDTO) {
        long empId = userContextDTO.getEmpId();
        long roleId = userContextDTO.getRoleId();
        long updaterEmpId = userContextDTO.getEmpId();
        employeeService.updateEmployee(roleId, empId, employeeDTO, updaterEmpId);
        return ResponseEntity.ok().body("Employee updated successfully");
    }

    @PutMapping("/update-password/{employeeId}")
    public ResponseEntity<?> updateEmployeePassword(@PathVariable Long employeeId, @RequestBody String newPassword, @AuthenticationPrincipal UserContextDTO userContextDTO) {
        long roleId = userContextDTO.getRoleId();
        long updaterEmpId = userContextDTO.getEmpId();
        employeeService.updatePassword(roleId, employeeId, newPassword, updaterEmpId);
        return ResponseEntity.ok().body("Employee password updated successfully");
    }

    @DeleteMapping("/delete/{employeeId}")
    public ResponseEntity<?> deleteEmployee(@PathVariable Long employeeId, @AuthenticationPrincipal UserContextDTO userContextDTO) {
        long roleId = userContextDTO.getRoleId();
        employeeService.deleteEmployee(roleId, employeeId);
        return ResponseEntity.ok().body("Employee deleted successfully");
    }
}
