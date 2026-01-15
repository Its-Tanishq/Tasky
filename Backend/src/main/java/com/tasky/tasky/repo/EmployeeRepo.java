package com.tasky.tasky.repo;

import com.tasky.tasky.dto.EmployeeDTO;
import com.tasky.tasky.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepo extends JpaRepository<Employee, Long> {

    Optional<Employee> findByEmail(String email);
    Optional<List<Employee>> findByRoleId(Long roleId);
    List<EmployeeDTO> findByIsActiveTrue();
    List<EmployeeDTO> findByIsActiveFalse();
}