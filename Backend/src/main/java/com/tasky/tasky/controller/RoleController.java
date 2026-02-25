package com.tasky.tasky.controller;

import com.tasky.tasky.dto.RoleDTO;
import com.tasky.tasky.dto.UserContextDTO;
import com.tasky.tasky.model.Role;
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

    @PostMapping("/create-role")
    public ResponseEntity<?> createRoles(@Valid @RequestBody RoleDTO role, @AuthenticationPrincipal UserContextDTO userContextDTO, HttpServletRequest request) {
        Long orgId = userContextDTO.getOrgId();
        String authHeader = request.getHeader("Authorization");
        String token = authHeader.substring(7);
        long roleId = userContextDTO.getRoleId();
        long empId = userContextDTO.getEmpId();
        roleService.createRole(roleId, role, orgId, empId);
        return ResponseEntity.ok().body("Roles created successfully");
    }

    @DeleteMapping("/delete-role/{id}")
    public ResponseEntity<?> deleteRole(@PathVariable int id, @AuthenticationPrincipal UserContextDTO userContextDTO) {
        long roleId = userContextDTO.getRoleId();
        roleService.deleteRole(roleId, id);
        return ResponseEntity.ok().body("Role deleted successfully");
    }

    @GetMapping("/get-all-roles")
    public ResponseEntity<?> getAllRoles(@AuthenticationPrincipal UserContextDTO userContextDTO) {
        Long orgId = userContextDTO.getOrgId();
        return ResponseEntity.ok().body(roleService.getAllRoles(orgId));
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
