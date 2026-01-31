package com.tasky.tasky.controller;

import com.tasky.tasky.dto.OrganizationDTO;
import com.tasky.tasky.dto.UserContextDTO;
import com.tasky.tasky.security.JWTUtil;
import com.tasky.tasky.service.OrganizationService;
import com.tasky.tasky.service.RefreshTokenService;
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
@RequestMapping("/api/organization")
public class OrganizationController {

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @PostMapping("/create-organization")
    public ResponseEntity<?> createOrganization(@Valid @RequestBody OrganizationDTO organizationDTO) {
        organizationService.createOrganization(organizationDTO);

        String accessToken = jwtUtil.generateAccessToken(organizationDTO.getEmail());
        String refreshToken = jwtUtil.generateRefreshToken(organizationDTO.getEmail());

        refreshTokenService.createRefreshToken(organizationDTO.getEmail(), refreshToken);

        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(false) // set true on https / production
                .path("/")
                .maxAge(-1)
                .sameSite("Lax") // set to None on production with https
                .build();

        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("message", "Organization Created Successfully");
        responseBody.put("accessToken", accessToken);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(responseBody);
    }

    @PostMapping("/login-organization")
    public ResponseEntity<?> loginOrganization(@RequestBody OrganizationDTO organizationDTO) {
        organizationService.loginOrganization(organizationDTO);

        String accessToken = jwtUtil.generateAccessToken(organizationDTO.getEmail());
        String refreshToken = jwtUtil.generateRefreshToken(organizationDTO.getEmail());

        refreshTokenService.createRefreshToken(organizationDTO.getEmail(), refreshToken);

        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(false) // set true on https / production
                .path("/")
                .maxAge(7 * 24 * 60 * 60)
                .sameSite("Lax") // set to None on production with https
                .build();

        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("message", "Organization Logged In Successfully");
        responseBody.put("accessToken", accessToken);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(responseBody);
    }

    @PutMapping("/update-organization")
    public ResponseEntity<?> updateOrganization(@RequestBody Map<String, String> updateRequest, @AuthenticationPrincipal UserContextDTO userContextDTO) {
        long roleId = userContextDTO.getRoleId();
        long updaterEmpId = userContextDTO.getEmpId();
        organizationService.updateOrganization(roleId, updateRequest.get("oldEmail"), updateRequest.get("newEmail"), updateRequest.get("newName"), updaterEmpId);
        return ResponseEntity.ok("Organization Updated Successfully");
    }

    @PutMapping("/update-password")
    public ResponseEntity<?> updatePassword(@RequestBody Map<String, String> updatePasswordRequest, @AuthenticationPrincipal UserContextDTO userContextDTO) {
        long roleId = userContextDTO.getRoleId();
        long updaterEmpId = userContextDTO.getEmpId();
        organizationService.updatePassword(roleId, updatePasswordRequest.get("email"), updatePasswordRequest.get("oldPassword"), updatePasswordRequest.get("newPassword"), updaterEmpId);
        return ResponseEntity.ok("Password Updated Successfully");
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody String email) {
        organizationService.forgotPassword(email);
        return ResponseEntity.ok("OTP Sent Successfully");
    }

    @PutMapping("/verify-otp")
    public ResponseEntity<?> verifyOTP(@RequestBody Map<String, String> otpRequest) {
        organizationService.verifyOTP(otpRequest.get("email"), Integer.parseInt(otpRequest.get("otp")));
        return ResponseEntity.ok("OTP Verified Successfully");
    }

    @PutMapping("/resend-otp")
    public ResponseEntity<?> resendOTP(@RequestBody String email) {
        organizationService.resendOTP(email);
        return ResponseEntity.ok("OTP Resent Successfully");
    }

    @PutMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> resetPasswordRequest) {
        organizationService.resetPassword(resetPasswordRequest.get("email"), resetPasswordRequest.get("newPassword"));
        return ResponseEntity.ok("Password Reset Successfully");
    }

    @PutMapping("/suspend-organization")
    public ResponseEntity<?> suspendOrganization(@RequestBody String email, @AuthenticationPrincipal UserContextDTO userContextDTO) {
        long roleId = userContextDTO.getRoleId();
        organizationService.suspendOrganization(roleId, email);
        return ResponseEntity.ok("Organization Suspended Successfully");
    }

    @PutMapping("/activate-organization")
    public ResponseEntity<?> activateOrganization(@RequestBody String email) {
        organizationService.activateOrganization(email);
        return ResponseEntity.ok("Organization Activated Successfully");
    }

    @PutMapping("/delete-organization")
    public ResponseEntity<?> deleteOrganization(@RequestBody String email, @AuthenticationPrincipal UserContextDTO userContextDTO) {
        long roleId = userContextDTO.getRoleId();
        organizationService.deleteOrganization(roleId, email);
        return ResponseEntity.ok("Organization Deleted Successfully");
    }


}
