package com.tasky.tasky.controller;

import com.tasky.tasky.dto.RoleDTO;
import com.tasky.tasky.dto.UserContextDTO;
import com.tasky.tasky.security.JWTUtil;
import com.tasky.tasky.service.RoleService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/role")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @Autowired
    private JWTUtil jwtUtil;

    @PostMapping("/create-roles")
    public ResponseEntity<?> createRoles(@Valid @RequestBody List<RoleDTO> roles, @AuthenticationPrincipal UserContextDTO userContextDTO, HttpServletRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        String authHeader = request.getHeader("Authorization");
        String token = authHeader.substring(7);
        long roleId = userContextDTO.getRoleId();
        long empId = userContextDTO.getEmpId();
        roleService.createRole(roleId, roles, email, empId);
        return ResponseEntity.ok().body("Roles created successfully");
    }

    @DeleteMapping("/delete-role/{id}")
    public ResponseEntity<?> deleteRole(@PathVariable int id, @AuthenticationPrincipal UserContextDTO userContextDTO) {
        long roleId = userContextDTO.getRoleId();
        roleService.deleteRole(roleId, id);
        return ResponseEntity.ok().body("Role deleted successfully");
    }

    @GetMapping("/get-all-roles")
    public ResponseEntity<?> getAllRoles() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok().body(roleService.getAllRoles(email));
    }

    @GetMapping("/get-permissions-by-role/{id}")
    public ResponseEntity<?> getPermissionsByRole(@PathVariable Long id) {
        return ResponseEntity.ok().body(roleService.getAllPermissionsByRole(id));
    }

    @PutMapping("/update-role/{id}")
    public ResponseEntity<?> updateRole(@PathVariable Long id, @Valid @RequestBody RoleDTO roleDTO, @AuthenticationPrincipal UserContextDTO userContextDTO) {
        long roleId = userContextDTO.getRoleId();
        long empId = userContextDTO.getEmpId();
        roleService.updateRole(roleId, id, roleDTO, empId);
        return ResponseEntity.ok().body("Role updated successfully");
    }
}
