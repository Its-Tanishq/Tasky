package com.tasky.tasky.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table
public class Announcement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private Organization organization;

    private String title;

    private String message;

    private String createdBy;

    private LocalDateTime createdAt;
}
