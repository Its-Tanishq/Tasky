package com.tasky.tasky.repo;

import com.tasky.tasky.dto.TeamDTO;
import com.tasky.tasky.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamRepo extends JpaRepository<Team, Long> {

    List<TeamDTO> findByCreatedBy(String createdBy);
}