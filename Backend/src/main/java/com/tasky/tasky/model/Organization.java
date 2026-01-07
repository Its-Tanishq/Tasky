package com.tasky.tasky.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table
public class Organization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    @Column(unique = true)
    private String email;
    
    @Enumerated(EnumType.STRING)
    private OrgStatus status = OrgStatus.ACTIVE;
    
    private LocalDateTime createdAt;
}
