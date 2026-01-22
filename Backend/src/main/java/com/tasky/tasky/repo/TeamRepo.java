package com.tasky.tasky.repo;

import com.tasky.tasky.dto.TeamDTO;
import com.tasky.tasky.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamRepo extends JpaRepository<Team, Long> {

    List<Team> findByOrganizationId(Long orgId);
}