package com.tasky.tasky.dto;

import com.tasky.tasky.model.TaskAssignType;
import com.tasky.tasky.model.TaskPriority;
import com.tasky.tasky.model.TaskStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class TaskDTO {

    private Long organizationId;

    @NotNull(message = "Task title cannot be null")
    private String title;

    @NotNull(message = "Task description cannot be null")
    private String description;

    private TaskStatus status;

    @NotNull(message = "Task priority cannot be null")
    private TaskPriority priority;

    @NotNull(message = "Task assign type cannot be null")
    private TaskAssignType assignType;

    private Long assignedEmployeeId;

    private Long assignedTeamId;

    private String createdBy;

    @NotNull(message = "Due date cannot be null")
    @DateTimeFormat
    private LocalDateTime dueDate;
}