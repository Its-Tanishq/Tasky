package com.tasky.tasky.service;

import com.tasky.tasky.dto.EmployeeDTO;
import com.tasky.tasky.exception.InvalidCredentialsException;
import com.tasky.tasky.model.Employee;
import com.tasky.tasky.model.Organization;
import com.tasky.tasky.model.Role;
import com.tasky.tasky.repo.EmployeeRepo;
import com.tasky.tasky.repo.OrganizationRepo;
import com.tasky.tasky.repo.RoleRepo;
import com.tasky.tasky.security.RequiresPermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepo employeeRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private OrganizationRepo organizationRepo;

    @Autowired
    private RoleRepo roleRepo;

    @RequiresPermission("CREATE_EMPLOYEE")
    public void createEmployee(Long roleId, EmployeeDTO employeeDTO, long employeeId) {
        Employee employee = mapToEntity(employeeDTO, employeeId); // EmployeeId mean who is creating employee

        try {
            employeeRepo.save(employee);
        } catch (Exception e) {
            throw new RuntimeException("Error creating employee: " + e.getMessage());
        }
    }

    public void loginEmployee(EmployeeDTO employeeDTO) {
        Employee employee = employeeRepo.findByEmail(employeeDTO.getEmail())
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        if (!passwordEncoder.matches(employeeDTO.getPassword(), employee.getPassword())) {
            throw new InvalidCredentialsException("Invalid Credentials");
        }
    }

    @RequiresPermission("UPDATE_EMPLOYEE")
    public void updateEmployee(Long roleId, Long employeeId, EmployeeDTO employeeDTO, Long updaterEmpId) {
        Employee employee = employeeRepo.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        Employee updaterEmployee = employeeRepo.findById(updaterEmpId)
                .orElseThrow(() -> new RuntimeException("Updater employee not found"));

        employee.setName(employeeDTO.getName());
        employee.setEmail(employeeDTO.getEmail());

        Role role = roleRepo.findByName(employeeDTO.getRole())
                .orElseThrow(() -> new RuntimeException("Role not found"));

        employee.setRole(role);
        employee.setUpdatedBy(updaterEmployee.getName());

        employeeRepo.save(employee);
    }

    @RequiresPermission("UPDATE_EMPLOYEE")
    public void updatePassword(Long roleId, Long employeeId, String newPassword, Long updaterEmpId) {
        Employee employee = employeeRepo.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        Employee updaterEmployee = employeeRepo.findById(updaterEmpId)
                .orElseThrow(() -> new RuntimeException("Updater employee not found"));

        employee.setPassword(passwordEncoder.encode(newPassword));
        employee.setUpdatedBy(updaterEmployee.getName());
        employeeRepo.save(employee);
    }

    @RequiresPermission("DELETE_EMPLOYEE")
    public void deleteEmployee(Long roleId, Long employeeId) {
        Employee employee = employeeRepo.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        employeeRepo.delete(employee);
    }

    private Employee mapToEntity(EmployeeDTO employeeDTO, long employeeId) {
        Employee employee = new Employee();
        employee.setName(employeeDTO.getName());
        employee.setEmail(employeeDTO.getEmail());
        employee.setPassword(passwordEncoder.encode(employeeDTO.getPassword()));

        Organization organization =  organizationRepo.findById(Long.valueOf(employeeDTO.getOrganizationId()))
                .orElseThrow(() -> new RuntimeException("Organization not found"));

        Role role = roleRepo.findByName(employeeDTO.getRole())
                .orElseThrow(() -> new RuntimeException("Role not found"));

        Employee employeeDetail = employeeRepo.findById(Long.valueOf(employeeId))
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        
        employee.setRole(role);
        employee.setOrganization(organization);
        employee.setCreatedBy(employeeDetail.getName());

        return employee;
    }
}
