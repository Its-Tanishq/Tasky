package com.tasky.tasky.service;

import com.tasky.tasky.model.Employee;
import com.tasky.tasky.repo.EmployeeRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailService implements UserDetailsService {

    @Autowired
    private EmployeeRepo employeeRepo;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Employee employee = employeeRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Employee not found with email: " + email));

        return org.springframework.security.core.userdetails.User
                .builder()
                .username(employee.getEmail())
                .password(employee.getPassword())
                .authorities(Collections.singletonList(() -> "ROLE_" + employee.getRole().getName()))
                .build();
    }
}
