package com.tasky.tasky.repo;

import com.tasky.tasky.model.Organization;
import com.tasky.tasky.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepo extends JpaRepository<Role, Long> {

    Optional<Role> findByName(String name);
    List<Role> findByOrganization(Organization organization);
}
