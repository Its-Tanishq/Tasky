package com.tasky.tasky.service;

import com.tasky.tasky.dto.OrganizationDTO;
import com.tasky.tasky.exception.DuplicateResourceException;
import com.tasky.tasky.exception.InvalidCredentialsException;
import com.tasky.tasky.exception.ResourceNotFoundException;
import com.tasky.tasky.model.*;
import com.tasky.tasky.repo.*;
import com.tasky.tasky.security.RequiresPermission;
import com.tasky.tasky.template.EmailTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrganizationService {

    @Autowired
    private OrganizationRepo organizationRepo;

    @Autowired
    private ResetPasswordOTPRepo resetPasswordOTPRepo;

    @Autowired
    private EmployeeRepo employeeRepo;

    @Autowired
    private RoleRepo roleRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Autowired
    private EmailTemplate emailTemplate;

    @Autowired
    private PermissionRepo permissionRepo;

    @Autowired
    private ObjectMapper objectMapper;

    public void createOrganization(OrganizationDTO organizationDTO) {
        Organization organization = mapToEntity(organizationDTO);

        if (organizationRepo.findByEmailAndStaus(organization.getEmail()).isPresent()) {
            throw new DuplicateResourceException("Organization with this email already exists");
        }
        if (organizationRepo.findByNameAndStatus(organization.getName()).isPresent()) {
            throw new DuplicateResourceException("Organization with this name already exists");
        }
        
        try {
            // Save organization first
            Organization savedOrganization = organizationRepo.save(organization);
            
            // Create owner role for the organization
            Role ownerRole = createOwnerRole(savedOrganization);
            
            // Create owner employee with same email and password
            createOwnerEmployee(organizationDTO, savedOrganization, ownerRole);
            
            emailService.sendHtmlMail(
                    organization.getEmail(),
                    "Welcome to Tasky - Your Organization Account is Active",
                    emailTemplate.buildWelcomeEmailTemplate(organization.getName())
            );
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void loginOrganization(OrganizationDTO organizationDTO) {
        // Check if employee exists with this email (since we now create employees for organizations)
        Employee employee = employeeRepo.findByEmail(organizationDTO.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Organization Not Found"));
        
        if (!passwordEncoder.matches(organizationDTO.getPassword(), employee.getPassword())) {
            throw new InvalidCredentialsException("Invalid Credentials");
        }

        Organization organization = employee.getOrganization();
        if (organization.getStatus() == OrgStatus.DELETED) {
            throw new RuntimeException("Organization is deleted");
        }
        if (organization.getStatus() == OrgStatus.SUSPENDED) {
            throw new RuntimeException("Organization is suspended");
        }
    }

    // We didn't allow to update password here
    @RequiresPermission("UPDATE_ORGANIZATION")
    public void updateOrganization(Long roleId, String oldMail, String newEmail, String newName, Long updaterEmpId) {
        Organization organization = organizationRepo.findByEmail(oldMail).orElseThrow(() -> new ResourceNotFoundException("Organization Not Found"));
        
        Employee updaterEmployee = employeeRepo.findById(updaterEmpId)
                .orElseThrow(() -> new RuntimeException("Updater employee not found"));
        
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
                throw new DuplicateResourceException("Organization with this email already exists");
            }
        }
        if (newName != null && !newName.isEmpty()) {
            if (organizationRepo.findByName(newName).isEmpty()) {
                organization.setName(newName);
            } else if (organizationRepo.findByName(newName).isPresent() && !newName.equals(organization.getName())) {
                throw new DuplicateResourceException("Organization with this name already exists");
            }
        }
        organization.setUpdatedBy(updaterEmployee.getName());
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

    @RequiresPermission("UPDATE_ORGANIZATION")
    public void updatePassword(Long roleId, String email, String oldPassword, String newPassword, Long updaterEmpId) {
        Organization organization = organizationRepo.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("Organization Not Found"));
        
        Employee updaterEmployee = employeeRepo.findById(updaterEmpId)
                .orElseThrow(() -> new RuntimeException("Updater employee not found"));
        
        if (!passwordEncoder.matches(oldPassword, organization.getPassword())) {
            throw new RuntimeException("Old Password is incorrect");
        }
        organization.setPassword(passwordEncoder.encode(newPassword));
        organization.setUpdatedBy(updaterEmployee.getName());
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

    @RequiresPermission("DELETE_ORGANIZATION")
    public void suspendOrganization(Long roleId, String email) {
        Role currentUserRole = roleRepo.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role Not Found"));
        
        if (!"OWNER".equals(currentUserRole.getName())) {
            throw new RuntimeException("Only organization owner can suspend the organization");
        }
        
        Organization organization = organizationRepo.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("Organization Not Found"));

        if (!currentUserRole.getOrganization().getId().equals(organization.getId())) {
            throw new RuntimeException("You can only suspend your own organization");
        }
        
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

    @RequiresPermission("DELETE_ORGANIZATION")
    public void deleteOrganization(Long roleId, String email) {
        Role currentUserRole = roleRepo.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role Not Found"));
        
        if (!"OWNER".equals(currentUserRole.getName())) {
            throw new RuntimeException("Only organization owner can delete the organization");
        }
        
        Organization organization = organizationRepo.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("Organization Not Found"));

        if (!currentUserRole.getOrganization().getId().equals(organization.getId())) {
            throw new RuntimeException("You can only delete your own organization");
        }
        
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
        organization.setCreatedBy("System");
        organization.setUpdatedBy("System");
        if (organizationDTO.getStatus() != null) {
            organization.setStatus(organizationDTO.getStatus());
        }
        return organization;
    }

    private Role createOwnerRole(Organization organization) {
        Role ownerRole = new Role();
        ownerRole.setName("OWNER");
        ownerRole.setOrganization(organization);
        return roleRepo.save(ownerRole);
    }

    private void createOwnerEmployee(OrganizationDTO organizationDTO, Organization organization, Role ownerRole) {
        Employee ownerEmployee = new Employee();
        ownerEmployee.setName(organizationDTO.getName());
        ownerEmployee.setEmail(organizationDTO.getEmail());
        ownerEmployee.setPassword(passwordEncoder.encode(organizationDTO.getPassword()));
        ownerEmployee.setOrganization(organization);
        ownerEmployee.setRole(ownerRole);
        ownerEmployee.setIsActive(true);

        List<Permission> permissions = permissionRepo.findAll();
        List<Long> ownerPermissions  = permissions.stream()
                        .map(Permission::getId)
                        .toList();

        try {
            ownerRole.setPermissions(objectMapper.writeValueAsString(ownerPermissions));
        } catch (Exception e) {
            throw new RuntimeException("Error setting owner permissions");
        }

        ownerEmployee.setCreatedBy("System");
        ownerEmployee.setUpdatedBy("System");

        employeeRepo.save(ownerEmployee);
    }

    public int generateOTP() {
        return (int)(Math.random() * 900000) + 100000;
    }
}
