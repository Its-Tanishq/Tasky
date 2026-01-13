package com.tasky.tasky.repo;

import com.tasky.tasky.model.Employee;
import com.tasky.tasky.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepo extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);
    void deleteByEmployee(Employee employee);
}
