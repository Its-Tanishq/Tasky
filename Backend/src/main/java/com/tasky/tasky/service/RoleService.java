package com.tasky.tasky.service;

import com.tasky.tasky.dto.RoleDTO;
import com.tasky.tasky.exception.DuplicateResourceException;
import com.tasky.tasky.model.Employee;
import com.tasky.tasky.model.Organization;
import com.tasky.tasky.model.Role;
import com.tasky.tasky.repo.EmployeeRepo;
import com.tasky.tasky.repo.OrganizationRepo;
import com.tasky.tasky.repo.PermissionRepo;
import com.tasky.tasky.repo.RoleRepo;
import com.tasky.tasky.security.JWTUtil;
import com.tasky.tasky.security.RequiresPermission;
import jakarta.validation.Valid;
import org.jetbrains.annotations.UnknownNullability;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RoleService {

    @Autowired
    private RoleRepo roleRepo;

    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private OrganizationRepo organizationRepo;

    @Autowired
    private EmployeeRepo employeeRepo;

    @Autowired
    private PermissionRepo permissionRepo;

    @Autowired
    private ObjectMapper objectMapper;

    @RequiresPermission("CREATE_ROLE")
    public void createRole(Long roleId, List<RoleDTO> roleList, String email, Long empId) {
        Organization organization = organizationRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Organization not found"));

        Employee employee = employeeRepo.findById(empId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        roleList.forEach(role -> {
            if (roleRepo.findByName(role.getName()).isPresent()) {
                throw new DuplicateResourceException("Role with name " + role + " already exists");
            }
            Role roleEntity = new Role();
            roleEntity.setName(role.getName());

            roleEntity.setPermissions(objectMapper.writeValueAsString(role.getPermissionIds()));

            roleEntity.setOrganization(organization);
            roleEntity.setCreatedBy(employee.getName());
            roleEntity.setUpdatedBy(employee.getName());
            roleRepo.save(roleEntity);
        });
    }

    @RequiresPermission("UPDATE_ROLE")
    public void updateRole(Long roleId, Long id, RoleDTO roleDTO, Long empId) {
        Role role = roleRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        Employee employee = employeeRepo.findById(empId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        Optional<Role> isExist = roleRepo.findByName(roleDTO.getName());

        if (isExist.isPresent() && isExist.get().getOrganization().getId().equals(role.getOrganization().getId())) {
            throw new DuplicateResourceException("Role with name " + roleDTO.getName() + " already exists");
        }

        role.setName(roleDTO.getName());
        role.setUpdatedBy(employee.getName());

        try {
            role.setPermissions(objectMapper.writeValueAsString(roleDTO.getPermissionIds()));
        } catch (Exception e) {
            throw new RuntimeException("Error updating permissions");
        }

        roleRepo.save(role);
    }

    public List<Long> getAllPermissionsByRole (Long id) {
        Role role = roleRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        List<Long> permissions;
        try {
            permissions = objectMapper.readValue(role.getPermissions(), ArrayList.class);
            return permissions;
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving permissions");
        }
    }

    @RequiresPermission("DELETE_ROLE")
    public void deleteRole(Long roleId, int id) {
        Role role = roleRepo.findById((long) id)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        Optional<List<Employee>> employee = employeeRepo.findByRoleId(role.getId());
        if (employee.isPresent() && !employee.get().isEmpty()) {
            throw new RuntimeException("You can not delete this role as it is assigned to employees");
        }
        roleRepo.delete(role);
    }

    public List<RoleDTO> getAllRoles(String email) {
        Organization organization = organizationRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Organization not found"));

        List<RoleDTO> roleList = new ArrayList<>();
        roleRepo.findByOrganization(organization).forEach(role -> {
            RoleDTO roleDTO = new RoleDTO();
            roleDTO.setName(role.getName());
            roleDTO.setOrganizationId(organization.getId());
            roleList.add(roleDTO);
        });

        return roleList;
    }
}
