package com.tasky.tasky.controller;

import com.tasky.tasky.dto.TeamDTO;
import com.tasky.tasky.dto.UserContextDTO;
import com.tasky.tasky.security.JWTUtil;
import com.tasky.tasky.service.TeamService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/team")
public class TeamController {

    @Autowired
    private TeamService teamService;

    @Autowired
    private JWTUtil jwtUtil;

    @PostMapping("/create")
    public ResponseEntity<?> createTeam(@RequestBody TeamDTO teamDTO, HttpServletRequest request, @AuthenticationPrincipal UserContextDTO userContextDTO) {
        long empId = userContextDTO.getEmpId();
        long roleId = userContextDTO.getRoleId();
        teamService.createTeam(roleId, teamDTO, empId);
        return ResponseEntity.ok("Team created successfully");
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateTeam(@RequestBody TeamDTO teamDTO, @PathVariable Long id, @AuthenticationPrincipal UserContextDTO userContextDTO) {
        long roleId = userContextDTO.getRoleId();
        long empId = userContextDTO.getEmpId();
        teamService.updateTeam(roleId, id, teamDTO, empId);
        return ResponseEntity.ok("Team updated successfully");
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteTeam(@PathVariable Long id, @AuthenticationPrincipal UserContextDTO userContextDTO) {
        long roleId = userContextDTO.getRoleId();
        teamService.deleteTeam(roleId, id);
        return ResponseEntity.ok("Team deleted successfully");
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllTeams(@AuthenticationPrincipal UserContextDTO userContextDTO) {
        long orgId = userContextDTO.getOrgId();
        return ResponseEntity.ok(teamService.getAllTeams(orgId));
    }

    @PutMapping("/add")
    public ResponseEntity<?> addInTeam(@RequestParam Long teamId, @RequestParam Long empId, @AuthenticationPrincipal UserContextDTO userContextDTO) {
        long roleId = userContextDTO.getRoleId();
        teamService.addInTeam(roleId, teamId, empId);
        return ResponseEntity.ok("Employee added to team successfully");
    }

    @PutMapping("/remove")
    public ResponseEntity<?> removeFromTeam(@RequestParam Long empId, @AuthenticationPrincipal UserContextDTO userContextDTO) {
        long roleId = userContextDTO.getRoleId();
        teamService.removeFromTeam(roleId, empId);
        return ResponseEntity.ok("Employee removed from team successfully");
    }
}
