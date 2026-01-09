package com.tasky.tasky.repo;

import com.tasky.tasky.model.OrgStatus;
import com.tasky.tasky.model.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrganizationRepo extends JpaRepository<Organization, Integer> {

    Optional<Organization> findByEmail(String email);
    Optional<Organization> findByName(String name);
}
