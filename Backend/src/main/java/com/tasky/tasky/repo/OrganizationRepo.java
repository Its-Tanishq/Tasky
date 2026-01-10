package com.tasky.tasky.repo;

import com.tasky.tasky.model.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrganizationRepo extends JpaRepository<Organization, Long> {

    @Query("SELECT o FROM Organization o WHERE o.email = :email ORDER BY o.id DESC LIMIT 1")
    Optional<Organization> findByEmail(String email);
    @Query("SELECT o FROM Organization o WHERE o.name = :name ORDER BY o.id DESC LIMIT 1")
    Optional<Organization> findByName(String name);
    @Query("SELECT o FROM Organization o WHERE o.email = :email AND o.status != 'DELETED' ORDER BY o.id DESC LIMIT 1")
    Optional<Organization> findByEmailAndStaus(String email);
    @Query("SELECT o FROM Organization o WHERE o.name = :name AND o.status != 'DELETED' ORDER BY o.id DESC LIMIT 1")
    Optional<Organization> findByNameAndStatus(String name);
}

