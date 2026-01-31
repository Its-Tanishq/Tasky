package com.tasky.tasky.dto;

import com.tasky.tasky.model.OrgStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class OrganizationDTO {

    @NotNull(message = "Organization name cannot be null")
    private String name;

    @NotNull(message = "Email cannot be null")
    @Email(message = "Email should be valid")
    private String email;

    @NotNull(message = "Password cannot be null")
    private String password;

    private OrgStatus status = OrgStatus.ACTIVE;

    private String createdBy;

    private String updatedBy;
}