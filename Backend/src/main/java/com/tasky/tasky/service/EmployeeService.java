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

import java.util.ArrayList;
import java.util.List;

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

    public List<EmployeeDTO> listOfEmployee(Long organizationId) {
        Organization organization = organizationRepo.findById(organizationId)
                .orElseThrow(() -> new RuntimeException("Organization not found"));

        List<EmployeeDTO> employeeList = new ArrayList<>();
        employeeRepo.findByOrganizationId(organization.getId()).forEach(employee -> {
            EmployeeDTO employeeDTO = new EmployeeDTO();
            employeeDTO.setId(employee.getId());
            employeeDTO.setName(employee.getName());
            employeeDTO.setEmail(employee.getEmail());
            employeeDTO.setRole(employee.getRole().getName());
            employeeList.add(employeeDTO);
        });
        return employeeList;
    }

    private Employee mapToEntity(EmployeeDTO employeeDTO, long employeeId) {
        Employee employee = new Employee();
        employee.setName(employeeDTO.getName());
        employee.setEmail(employeeDTO.getEmail());
        employee.setPassword(passwordEncoder.encode(employeeDTO.getPassword()));

        Employee employeeDetail = employeeRepo.findById(Long.valueOf(employeeId))
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        Organization organization;
        if (employeeDTO.getOrganizationId() != null && !employeeDTO.getOrganizationId().isEmpty()) {
            organization = organizationRepo.findById(Long.valueOf(employeeDTO.getOrganizationId()))
                    .orElseThrow(() -> new RuntimeException("Organization not found"));
        } else {
            organization = employeeDetail.getOrganization();
        }

        Role role = roleRepo.findByName(employeeDTO.getRole())
                .orElseThrow(() -> new RuntimeException("Role not found"));
        
        employee.setRole(role);
        employee.setOrganization(organization);
        employee.setCreatedBy(employeeDetail.getName());

        return employee;
    }
}
