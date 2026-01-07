package com.tasky.tasky.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "organization_id")
    private Organization organization;

    private LocalDateTime createdAt;
}