package com.tasky.tasky.service;

import com.tasky.tasky.model.Permission;
import com.tasky.tasky.model.Role;
import com.tasky.tasky.repo.PermissionRepo;
import com.tasky.tasky.repo.RoleRepo;
import com.tasky.tasky.security.RequiresPermission;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Aspect
@Component
@Service
public class PermissionService {

    @Autowired
    private PermissionRepo permissionRepo;

    @Autowired
    private RoleRepo roleRepo;

    // Initialize Permissions using permission.sql file, available in main directory of backend folder. Bcz we have default permission, no one can create or delete permission, They can only give or revoke permission to roles.

    public List<Permission> getAllPermissions() {
        List<Permission> permissions = permissionRepo.findAll();
        return permissions;
    }
}
