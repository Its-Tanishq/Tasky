package com.tasky.tasky.controller;

import com.tasky.tasky.dto.RoleDTO;
import com.tasky.tasky.security.JWTUtil;
import com.tasky.tasky.service.RoleService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<?> createRoles(@Valid @RequestBody List<RoleDTO> roles, HttpServletRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        String authHeader = request.getHeader("Authorization");
        String token = authHeader.substring(7);
        Long roleId = (long) jwtUtil.getRoleIdFromToken(token);
        roleService.createRole(roleId, roles, email);
        return ResponseEntity.ok().body("Roles created successfully");
    }

    @DeleteMapping("/delete-role/{id}")
    public ResponseEntity<?> deleteRole(@PathVariable int id) {
        roleService.deleteRole(id);
        return ResponseEntity.ok().body("Role deleted successfully");
    }

    @GetMapping("/get-all-roles")
    public ResponseEntity<?> getAllRoles() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok().body(roleService.getAllRoles(email));
    }

    @GetMapping("/get-permissions-by-role/{roleId}")
    public ResponseEntity<?> getPermissionsByRole(@PathVariable Long roleId) {
        return ResponseEntity.ok().body(roleService.getAllPermissionsByRole(roleId));
    }

    @PutMapping("/update-role/{roleId}")
    public ResponseEntity<?> updateRole(@PathVariable Long roleId, @Valid @RequestBody RoleDTO roleDTO) {
        roleService.updateRole(roleId, roleDTO);
        return ResponseEntity.ok().body("Role updated successfully");
    }
}
