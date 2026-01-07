package com.tasky.tasky.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;

    private String name;

    @Column(unique = true)
    private String email;

    private String password;

    private Boolean isActive = false;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    @ManyToOne
    @JoinColumn(name = "organization_id")
    private Organization organization;

    private LocalDateTime createdAt;
}
