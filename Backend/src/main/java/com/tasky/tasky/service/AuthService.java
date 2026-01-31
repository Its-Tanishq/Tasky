package com.tasky.tasky.service;

import com.tasky.tasky.model.Employee;
import com.tasky.tasky.repo.EmployeeRepo;
import com.tasky.tasky.repo.OrganizationRepo;
import com.tasky.tasky.repo.RoleRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    @Autowired
    private EmployeeRepo employeeRepo;

    public Map<String, String> myDetails(Long empId) {
        Map<String, String> me = new HashMap<>();

        Employee employee = employeeRepo.findById(empId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        me.put("name", employee.getName());
        me.put("email", employee.getEmail());
        me.put("orgName", employee.getOrganization().getName());
        me.put("role", employee.getRole().getName());
        me.put("orgStatus", String.valueOf(employee.getOrganization().getStatus()));

        return me;
    }
}
