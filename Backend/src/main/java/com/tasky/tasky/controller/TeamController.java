package com.tasky.tasky.controller;

import com.tasky.tasky.dto.TeamDTO;
import com.tasky.tasky.security.JWTUtil;
import com.tasky.tasky.service.TeamService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/team")
public class TeamController {

    @Autowired
    private TeamService teamService;

    @Autowired
    private JWTUtil jwtUtil;

    @PostMapping("/create")
    public ResponseEntity<?> createTeam(@RequestBody TeamDTO teamDTO, HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        String token = authHeader.substring(7);
        long empId = jwtUtil.getEmpIdFromToken(token);
        teamService.createTeam(teamDTO, empId);
        return ResponseEntity.ok("Team created successfully");
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateTeam(@RequestBody TeamDTO teamDTO, @PathVariable Long id) {
        teamService.updateTeam(id, teamDTO);
        return ResponseEntity.ok("Team updated successfully");
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteTeam(@PathVariable Long id) {
        teamService.deleteTeam(id);
        return ResponseEntity.ok("Team deleted successfully");
    }

    @GetMapping("/all/{id}")
    public ResponseEntity<?> getAllTeams(@PathVariable Long id) {
        return ResponseEntity.ok(teamService.getAllTeams(id));
    }

    @PutMapping("/add")
    public ResponseEntity<?> addInTeam(@RequestParam Long teamId, @RequestParam Long empId) {
        teamService.addInTeam(teamId, empId);
        return ResponseEntity.ok("Employee added to team successfully");
    }

    @PutMapping("/remove")
    public ResponseEntity<?> removeFromTeam(@RequestParam Long empId) {
        teamService.removeFromTeam(empId);
        return ResponseEntity.ok("Employee removed from team successfully");
    }
}
