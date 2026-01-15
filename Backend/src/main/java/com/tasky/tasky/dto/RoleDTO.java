package com.tasky.tasky.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class RoleDTO {

    @NotNull(message = "Role name cannot be null")
    private String name;

    private Long organizationId;
}