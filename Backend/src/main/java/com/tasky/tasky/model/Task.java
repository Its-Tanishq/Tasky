package com.tasky.tasky.model;

import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Entity
@Table
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "organization_id")
    private Organization organization;

    private String title;

    private String description;

    @Enumerated(EnumType.STRING)
    private TaskStatus status = TaskStatus.PENDING;

    @Enumerated(EnumType.STRING)
    private TaskPriority priority;

    @Enumerated(EnumType.STRING)
    private TaskAssignType assignType;

    @ManyToOne
    @JoinColumn(name = "assigned_employee_id")
    private Employee assignedEmployee;

    @ManyToOne
    @JoinColumn(name = "assigned_team_id")
    private Team assignedTeam;

    private String createdBy;

    @DateTimeFormat
    private LocalDateTime dueDate;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
