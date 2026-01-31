package com.tasky.tasky.service;

import com.tasky.tasky.dto.TeamDTO;
import com.tasky.tasky.model.Employee;
import com.tasky.tasky.model.Team;
import com.tasky.tasky.repo.EmployeeRepo;
import com.tasky.tasky.repo.OrganizationRepo;
import com.tasky.tasky.repo.TeamRepo;
import com.tasky.tasky.security.RequiresPermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeamService {

    @Autowired
    private TeamRepo teamRepo;

    @Autowired
    private EmployeeRepo employeeRepo;

    @Autowired
    private OrganizationRepo organizationRepo;

    @RequiresPermission("CREATE_TEAM")
    public void createTeam(Long roleId, TeamDTO teamDTO, Long empId) {
        Team team = mapToEntity(teamDTO, empId);
        try {
            teamRepo.save(team);
        } catch (Exception e) {
            throw new RuntimeException("Error creating team: " + e.getMessage());
        }
    }

    @RequiresPermission("UPDATE_TEAM")
    public void updateTeam(Long roleId, Long teamId, TeamDTO teamDTO, Long empId) {
        Team existingTeam = teamRepo.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found"));
        
        Employee employee = employeeRepo.findById(empId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        
        existingTeam.setName(teamDTO.getName());
        existingTeam.setUpdatedBy(employee.getName());
        try {
            teamRepo.save(existingTeam);
        } catch (Exception e) {
            throw new RuntimeException("Error updating team: " + e.getMessage());
        }
    }

    @RequiresPermission("DELETE_TEAM")
    public void deleteTeam(Long roleId, Long teamId) {
        Team team = teamRepo.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found"));
        teamRepo.delete(team);
    }

    public List<Team> getAllTeams(Long orgId) {
        List<Team> teams = teamRepo.findByOrganizationId(orgId);
        return teams;
    }

    @RequiresPermission("UPDATE_TEAM")
    public void addInTeam(Long roleId, Long teamId, Long empId) {
        Team team = teamRepo.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found"));
        Employee employee = employeeRepo.findById(empId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        employee.setTeam(team);
        employeeRepo.save(employee);
    }

    @RequiresPermission("UPDATE_TEAM")
    public void removeFromTeam(Long roleId, Long empId) {
        Employee employee = employeeRepo.findById(empId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        employee.setTeam(null);
        employeeRepo.save(employee);
    }

    private Team mapToEntity(TeamDTO teamDTO, Long empId) {
        Team team = new Team();
        team.setName(teamDTO.getName());
        Employee employee = employeeRepo.findById(empId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        team.setCreatedBy(employee.getName());
        team.setOrganization(employee.getOrganization());
        return team;
    }

}
