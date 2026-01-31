package com.tasky.tasky.controller;

import com.tasky.tasky.dto.AnnouncementDTO;
import com.tasky.tasky.dto.UserContextDTO;
import com.tasky.tasky.security.JWTUtil;
import com.tasky.tasky.service.AnnouncementService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/announcement")
public class AnnouncementController {

    @Autowired
    private AnnouncementService announcementService;

    @Autowired
    private JWTUtil jwtUtil;

    @PostMapping("/create")
    public ResponseEntity<?> createAnnouncement(@Valid @RequestBody AnnouncementDTO announcementDTO, @AuthenticationPrincipal UserContextDTO userContextDTO) {
        long empId = userContextDTO.getEmpId();
        long roleId = userContextDTO.getRoleId();
        announcementService.createAnnouncement(roleId, announcementDTO, empId);
        return ResponseEntity.ok().body("Announcement created successfully");
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateAnnouncement(@Valid @RequestBody AnnouncementDTO announcementDTO, @PathVariable long id, @AuthenticationPrincipal UserContextDTO userContextDTO) {
        long roleId = userContextDTO.getRoleId();
        long empId = userContextDTO.getEmpId();
        announcementService.updateAnnouncement(roleId, id, announcementDTO, empId);
        return ResponseEntity.ok().body("Announcement updated successfully");
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteAnnouncement(@PathVariable long id, @AuthenticationPrincipal UserContextDTO userContextDTO) {
        long roleId = userContextDTO.getRoleId();
        announcementService.deleteAnnouncement(roleId, id);
        return ResponseEntity.ok().body("Announcement deleted successfully");
    }

    @GetMapping("/organization")
    public ResponseEntity<?> getAnnouncements(@AuthenticationPrincipal UserContextDTO userContextDTO) {
        long orgId = userContextDTO.getOrgId();
        long roleId = userContextDTO.getRoleId();
        return ResponseEntity.ok().body(announcementService.getAnnouncements(roleId, orgId));
    }
}
