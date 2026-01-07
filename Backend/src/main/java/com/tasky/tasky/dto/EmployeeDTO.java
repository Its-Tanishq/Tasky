package com.tasky.tasky.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class EmployeeDTO {

    @NotNull(message = "Employee name cannot be null")
    private String name;

    @NotNull(message = "Email cannot be null")
    @Email(message = "Email should be valid")
    private String email;

    @NotNull(message = "Password cannot be null")
    private String password;

    private Boolean isActive = false;

    @NotNull(message = "Role cannot be null")
    private String role;

    @NotNull(message = "Organization cannot be null")
    private String organizationId;
}
