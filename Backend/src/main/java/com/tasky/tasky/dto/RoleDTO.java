package com.tasky.tasky.dto;

import com.tasky.tasky.model.Permission;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class RoleDTO {

    @NotNull(message = "Role name cannot be null")
    private String name;

    private List<Long> permissionIds;

    private Long organizationId;

    private String createdBy;

    private String updatedBy;
}