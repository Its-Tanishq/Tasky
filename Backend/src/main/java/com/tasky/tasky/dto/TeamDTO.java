package com.tasky.tasky.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class TeamDTO {

    @NotNull(message = "Team name cannot be null")
    private String name;

    private String createdBy;

    @NotNull(message = "Organization ID cannot be null")
    private Long organizationId;
}