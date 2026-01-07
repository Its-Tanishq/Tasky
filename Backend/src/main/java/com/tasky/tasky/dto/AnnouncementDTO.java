package com.tasky.tasky.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class AnnouncementDTO {

    @NotNull(message = "Organization ID cannot be null")
    private Long organizationId;

    @NotNull(message = "Announcement title cannot be null")
    private String title;

    @NotNull(message = "Announcement content cannot be null")
    private String message;

    @NotNull(message = "Creator information cannot be null")
    private String createdBy;
}
