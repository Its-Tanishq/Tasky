package com.tasky.tasky.service;

import com.tasky.tasky.dto.RoleDTO;
import com.tasky.tasky.model.Employee;
import com.tasky.tasky.model.Organization;
import com.tasky.tasky.model.Role;
import com.tasky.tasky.repo.EmployeeRepo;
import com.tasky.tasky.repo.OrganizationRepo;
import com.tasky.tasky.repo.RoleRepo;
import com.tasky.tasky.security.JWTUtil;
import jakarta.validation.Valid;
import org.jetbrains.annotations.UnknownNullability;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public void createRole(@Valid @UnknownNullability List<RoleDTO> roleList, String email) {
        Organization organization = organizationRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Organization not found"));
        
        roleList.forEach(role -> {
            if (roleRepo.findByName(role.getName()).isPresent()) {
                throw new RuntimeException("Role with name " + role + " already exists");
            }
            Role roleEntity = new Role();
            roleEntity.setName(role.getName());
            roleEntity.setOrganization(organization);
            roleRepo.save(roleEntity);
        });
    }

    public void deleteRole(int id) {
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
