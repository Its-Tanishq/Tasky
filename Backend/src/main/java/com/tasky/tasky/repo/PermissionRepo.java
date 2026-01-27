package com.tasky.tasky.repo;

import com.tasky.tasky.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PermissionRepo extends JpaRepository<Permission, Integer> {

    Permission findByName(String name);
}
