package com.tasky.tasky.service;

import com.tasky.tasky.model.Employee;
import com.tasky.tasky.model.Permission;
import com.tasky.tasky.repo.EmployeeRepo;
import com.tasky.tasky.repo.OrganizationRepo;
import com.tasky.tasky.repo.PermissionRepo;
import com.tasky.tasky.repo.RoleRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.security.Permissions;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AuthService {

    @Autowired
    private EmployeeRepo employeeRepo;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PermissionRepo permissionRepo;

    public Map<String, String> myDetails(Long empId) {
        Map<String, String> me = new HashMap<>();

        Employee employee = employeeRepo.findById(empId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        me.put("name", employee.getName());
        me.put("email", employee.getEmail());
        me.put("orgName", employee.getOrganization().getName());
        me.put("role", employee.getRole().getName());
        me.put("orgStatus", String.valueOf(employee.getOrganization().getStatus()));

        try {
            List<Object> rawPermissions = objectMapper.readValue(employee.getRole().getPermissions(), ArrayList.class);
            List<Long> permissionIds = rawPermissions.stream()
                    .map(obj -> Long.parseLong(obj.toString()))
                    .collect(Collectors.toList());
            List<Permission> permissionList = permissionRepo.findAllById(permissionIds);
            List<String> permissionNames = permissionList.stream()
                    .map(Permission::getName)
                    .collect(Collectors.toList());
            
            me.put("permissions", objectMapper.writeValueAsString(permissionNames));
        } catch (Exception e) {
            throw new RuntimeException("Error processing permissions", e);
        }

        return me;
    }
}
