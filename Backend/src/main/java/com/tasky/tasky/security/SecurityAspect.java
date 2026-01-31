package com.tasky.tasky.security;

import com.tasky.tasky.exception.InsufficientPermissionException;
import com.tasky.tasky.model.Permission;
import com.tasky.tasky.model.Role;
import com.tasky.tasky.repo.PermissionRepo;
import com.tasky.tasky.repo.RoleRepo;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@Aspect
@Component
public class SecurityAspect {

    @Autowired
    private PermissionRepo permissionRepo;

    @Autowired
    private RoleRepo roleRepo;

    @Autowired
    private ObjectMapper objectMapper;

    @Before("@annotation(requiresPermission)")
    public void checkPermission(JoinPoint joinPoint, RequiresPermission requiresPermission) {
        String permissionName = requiresPermission.value();

        Long roleId = (Long) joinPoint.getArgs()[0]; // Role ID Will be first argument in every method

        Role role = roleRepo.findById(roleId).orElse(null);

        Permission permission = permissionRepo.findByName(permissionName);
        if (permission == null) {
            throw new RuntimeException("Permission not found: " + permissionName);
        }

        if (role != null) {
            String permissionIds = role.getPermissions();
            try {
                List<Long> permissionIdList = objectMapper.readValue(permissionIds, new TypeReference<List<Long>>() {});
                if (permissionIdList.contains(permission.getId())) {
                    return;
                }
            } catch (Exception e) {
                throw new RuntimeException("Error parsing permissions for role: " + role.getName(), e);
            }
        }

        throw new InsufficientPermissionException("Insufficient Permissions");
    }
}
