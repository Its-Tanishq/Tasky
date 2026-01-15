package com.tasky.tasky.controller;

import com.tasky.tasky.dto.RoleDTO;
import com.tasky.tasky.service.RoleService;
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

    @PostMapping("/create-roles")
    public ResponseEntity<?> createRoles(@Valid @RequestBody List<RoleDTO> roles) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        roleService.createRole(roles, email);
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
}
