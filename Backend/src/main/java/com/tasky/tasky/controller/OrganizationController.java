package com.tasky.tasky.controller;

import com.tasky.tasky.dto.OrganizationDTO;
import com.tasky.tasky.service.OrganizationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/organization")
public class OrganizationController {

    @Autowired
    private OrganizationService organizationService;

    @PostMapping("/create-organization")
    public ResponseEntity<?> createOrganization(@Valid @RequestBody OrganizationDTO orgnizationDTO) {
        organizationService.createOrganization(orgnizationDTO);
        return ResponseEntity.ok("Organization Created Successfully");
    }

    @PostMapping("/login-organization")
    public ResponseEntity<?> loginOrganization(@RequestBody OrganizationDTO orgnizationDTO) {
        organizationService.loginOrganization(orgnizationDTO);
        return ResponseEntity.ok("Organization Logged In Successfully");
    }

    @PutMapping("/update-organization")
    public ResponseEntity<?> updateOrganization(@RequestBody Map<String, String> updateRequest) {
        organizationService.updateOrganization(updateRequest.get("oldEmail"), updateRequest.get("newEmail"), updateRequest.get("newName"));
        return ResponseEntity.ok("Organization Updated Successfully");
    }

    @PutMapping("/update-password")
    public ResponseEntity<?> updatePassword(@RequestBody Map<String, String> updatePasswordRequest) {
        organizationService.updatePassword(updatePasswordRequest.get("email"), updatePasswordRequest.get("oldPassword"), updatePasswordRequest.get("newPassword"));
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
    public ResponseEntity<?> suspendOrganization(@RequestBody String email) {
        organizationService.suspendOrganization(email);
        return ResponseEntity.ok("Organization Suspended Successfully");
    }

    @PutMapping("/activate-organization")
    public ResponseEntity<?> activateOrganization(@RequestBody String email) {
        organizationService.activateOrganization(email);
        return ResponseEntity.ok("Organization Activated Successfully");
    }

    @PutMapping("/delete-organization")
    public ResponseEntity<?> deleteOrganization(@RequestBody String email) {
        organizationService.deleteOrganization(email);
        return ResponseEntity.ok("Organization Deleted Successfully");
    }


}
