package com.tasky.tasky.service;

import com.tasky.tasky.dto.EmployeeDTO;
import com.tasky.tasky.model.Employee;
import com.tasky.tasky.model.Organization;
import com.tasky.tasky.model.Role;
import com.tasky.tasky.repo.EmployeeRepo;
import com.tasky.tasky.repo.OrganizationRepo;
import com.tasky.tasky.repo.RoleRepo;
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

    public void createEmployee(EmployeeDTO employeeDTO, int employeeId) {
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
            throw new RuntimeException("Invalid Credentials");
        }
    }

    public void updateEmployee(Long employeeId, EmployeeDTO employeeDTO) {
        Employee employee = employeeRepo.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        employee.setName(employeeDTO.getName());
        employee.setEmail(employeeDTO.getEmail());

        Organization organization =  organizationRepo.findById(Long.valueOf(employeeDTO.getOrganizationId()))
                .orElseThrow(() -> new RuntimeException("Organization not found"));

        Role role = roleRepo.findByName(employeeDTO.getRole())
                .orElseThrow(() -> new RuntimeException("Role not found"));

        employee.setRole(role);

        employeeRepo.save(employee);
    }

    public void updatePassword(Long employeeId, String newPassword) {
        Employee employee = employeeRepo.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        employee.setPassword(passwordEncoder.encode(newPassword));
        employeeRepo.save(employee);
    }

    public void deleteEmployee(Long employeeId) {
        Employee employee = employeeRepo.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        employeeRepo.delete(employee);
    }

    private Employee mapToEntity(EmployeeDTO employeeDTO, int employeeId) {
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
