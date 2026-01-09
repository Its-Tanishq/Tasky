package com.tasky.tasky.service;

import com.tasky.tasky.dto.OrganizationDTO;
import com.tasky.tasky.exception.ResourceNotFoundException;
import com.tasky.tasky.model.OrgStatus;
import com.tasky.tasky.model.Organization;
import com.tasky.tasky.model.ResetPasswordOTP;
import com.tasky.tasky.repo.OrganizationRepo;
import com.tasky.tasky.repo.ResetPasswordOTPRepo;
import com.tasky.tasky.template.EmailTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class OrganizationService {

    @Autowired
    private OrganizationRepo organizationRepo;

    @Autowired
    private ResetPasswordOTPRepo resetPasswordOTPRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Autowired
    private EmailTemplate emailTemplate;

    // Need to update if available organization email is deleted then allow to create organization with same email
    public void createOrganization(OrganizationDTO organizationDTO) {
        Organization organization = mapToEntity(organizationDTO);
        try {
            organizationRepo.save(organization);
            emailService.sendHtmlMail(
                    organization.getEmail(),
                    "Welcome to Tasky - Your Organization Account is Active",
                    emailTemplate.buildWelcomeEmailTemplate(organization.getName())
            );
        } catch (DataIntegrityViolationException e) {
            String errorMessage = e.getMessage();
            if (errorMessage.contains("duplicate key value") && errorMessage.contains("Key (email)=") && errorMessage.contains("Key (name)=")) {
                throw new RuntimeException("Organization with this email and name already exists");
            } else if (errorMessage.contains("duplicate key value") && errorMessage.contains("Key (email)=")) {
                throw new RuntimeException("Organization with this email already exists");
            } else if (errorMessage.contains("duplicate key value") && errorMessage.contains("Key (name)=")) {
                throw new RuntimeException("Organization with this name already exists");
            }
            throw new RuntimeException("Failed to create organization: " + errorMessage);
        }
    }

    public void loginOrganization(OrganizationDTO organizationDTO) {
        Organization organization = organizationRepo.findByEmail(organizationDTO.getEmail()).orElseThrow(() -> new ResourceNotFoundException("Organization Not Found"));
        if (!passwordEncoder.matches(organizationDTO.getPassword(), organization.getPassword())) {
            throw new RuntimeException("Invalid Credentials");
        }
        if (organization.getStatus() == OrgStatus.DELETED) {
            throw new RuntimeException("Organization is deleted");
        }
    }

    // We didn't allow to update password here
    public void updateOrganization(String oldMail, String newEmail, String newName) {
        Organization organization = organizationRepo.findByEmail(oldMail).orElseThrow(() -> new ResourceNotFoundException("Organization Not Found"));
        if (organization.getStatus() == OrgStatus.SUSPENDED) {
            throw new RuntimeException("Organization is suspended you cannot update information");
        }
        // Just for safety check
        if (organization.getStatus() == OrgStatus.DELETED) {
            throw new RuntimeException("Organization is deleted you cannot update information");
        }
        if (newEmail != null && !newEmail.isEmpty()) {
            if (organizationRepo.findByEmail(newEmail).isEmpty()) {
                organization.setEmail(newEmail);
            } else if (organizationRepo.findByEmail(newEmail).isPresent() && !newEmail.equals(oldMail)) {
                throw new RuntimeException("Organization with this email already exists");
            }
        }
        if (newName != null && !newName.isEmpty()) {
            if (organizationRepo.findByName(newName).isEmpty()) {
                organization.setName(newName);
            } else if (organizationRepo.findByName(newName).isPresent() && !newName.equals(organization.getName())) {
                throw new RuntimeException("Organization with this name already exists");
            }
        }
        try {
            organizationRepo.save(organization);
            emailService.sendHtmlMail(
                    newEmail,
                    "Organization Information Updated - Tasky",
                    emailTemplate.buildOrganizationUpdateTemplate(organization.getName())
            );
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void updatePassword(String email, String oldPassword, String newPassword) {
        Organization organization = organizationRepo.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("Organization Not Found"));
        if (!passwordEncoder.matches(oldPassword, organization.getPassword())) {
            throw new RuntimeException("Old Password is incorrect");
        }
        organization.setPassword(passwordEncoder.encode(newPassword));
        try {
            organizationRepo.save(organization);
            emailService.sendHtmlMail(
                    organization.getEmail(),
                    "Password Updated Successfully - Tasky",
                    emailTemplate.buildPasswordUpdateTemplate(organization.getName())
            );
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void forgotPassword(String email) {
        Organization organization = organizationRepo.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("Organization Not Found"));
        if (organization.getStatus() == OrgStatus.SUSPENDED) {
            throw new RuntimeException("Organization is suspended you cannot reset password");
        }
        if (organization.getStatus() == OrgStatus.DELETED) {
            throw new RuntimeException("Organization is deleted you cannot reset password");
        }

        int otp = generateOTP();
        LocalDateTime expireTime = LocalDateTime.now().plusMinutes(10);

        System.out.println("OTP is: " + otp);

        ResetPasswordOTP resetPasswordOTP = new ResetPasswordOTP();
        resetPasswordOTP.setOTP(otp);
        resetPasswordOTP.setEmail(email);
        resetPasswordOTP.setExpiryTime(expireTime);

        try {
            resetPasswordOTPRepo.save(resetPasswordOTP);
            emailService.sendHtmlMail(
                    organization.getEmail(),
                    "Password Reset OTP - Tasky",
                    emailTemplate.buildPasswordResetOtpTemplate(organization.getName(), otp)
            );
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void verifyOTP(String email, int otp) {
        ResetPasswordOTP resetPasswordOTP = resetPasswordOTPRepo.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("OTP Not Found"));
        if (resetPasswordOTP.getOTP() == otp) {
            if (resetPasswordOTP.getExpiryTime().isBefore(LocalDateTime.now())) {
                throw new RuntimeException("OTP has expired");
            }
            if (resetPasswordOTP.getIsUsed()) {
                throw new RuntimeException("OTP has already been used");
            }
            resetPasswordOTP.setIsUsed(true);
            resetPasswordOTPRepo.save(resetPasswordOTP);
        } else {
            throw new RuntimeException("Invalid OTP");
        }
    }

    public void resendOTP(String email) {
        ResetPasswordOTP resetPasswordOTP = resetPasswordOTPRepo.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("Previous OTP Not Found"));
        if (resetPasswordOTP.getExpiryTime().isBefore(LocalDateTime.now())) {
            forgotPassword(email);
        } else {
            throw new RuntimeException("Previous OTP is still valid");
        }
    }

    public void resetPassword(String email, String newPassword) {
        Organization organization = organizationRepo.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("Organization Not Found"));

        organization.setPassword(passwordEncoder.encode(newPassword));
        try  {
            organizationRepo.save(organization);
            emailService.sendHtmlMail(
                    organization.getEmail(),
                    "Password Reset Successfully - Tasky",
                    emailTemplate.buildPasswordResetSuccessTemplate(organization.getName())
            );
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void suspendOrganization(String email) {
        Organization organization = organizationRepo.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("Organization Not Found"));
        if (organization.getStatus() == OrgStatus.SUSPENDED) {
            throw new RuntimeException("Organization is already suspended");
        }
        // Just for safety check
        if (organization.getStatus() == OrgStatus.DELETED) {
            throw new RuntimeException("Organization is deleted");
        }
        organization.setStatus(OrgStatus.SUSPENDED);
        try {
            organizationRepo.save(organization);
            emailService.sendHtmlMail(
                    organization.getEmail(),
                    "Organization Account Suspended - Tasky",
                    emailTemplate.buildOrganizationSuspendTemplate(organization.getName())
            );
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void activateOrganization(String email) {
        Organization organization = organizationRepo.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("Organization Not Found"));
        if (organization.getStatus() == OrgStatus.ACTIVE) {
            throw new RuntimeException("Organization is already active");
        }
        // Just for safety check
        if (organization.getStatus() == OrgStatus.DELETED) {
            throw new RuntimeException("Organization is deleted");
        }
        organization.setStatus(OrgStatus.ACTIVE);
        try {
            organizationRepo.save(organization);
            emailService.sendHtmlMail(
                    organization.getEmail(),
                    "Organization Account Activated - Tasky",
                    emailTemplate.buildOrganizationActivateTemplate(organization.getName())
            );
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void deleteOrganization(String email) {
        Organization organization = organizationRepo.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("Organization Not Found"));
        // Just for safety check
        if (organization.getStatus() == OrgStatus.DELETED) {
            throw new RuntimeException("Organization is already deleted");
        }
        organization.setStatus(OrgStatus.DELETED);
        try {
            organizationRepo.save(organization);
            emailService.sendHtmlMail(
                    organization.getEmail(),
                    "Organization Account Deleted - Tasky",
                    emailTemplate.buildOrganizationDeleteTemplate(organization.getName())
            );
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private Organization mapToEntity(OrganizationDTO organizationDTO) {
        Organization organization = new Organization();
        organization.setName(organizationDTO.getName());
        if (organizationDTO.getPassword() != null) {
            organization.setPassword(passwordEncoder.encode(organizationDTO.getPassword()));
        }
        organization.setEmail(organizationDTO.getEmail());
        if (organizationDTO.getStatus() != null) {
            organization.setStatus(organizationDTO.getStatus());
        }
        return organization;
    }


    public int generateOTP() {
        return (int)(Math.random() * 900000) + 100000;
    }
}
