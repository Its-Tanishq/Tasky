package com.tasky.tasky.controller;

import com.tasky.tasky.dto.AnnouncementDTO;
import com.tasky.tasky.security.JWTUtil;
import com.tasky.tasky.service.AnnouncementService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/announcement")
public class AnnouncementController {

    @Autowired
    private AnnouncementService announcementService;

    @Autowired
    private JWTUtil jwtUtil;

    @PostMapping("/create")
    public ResponseEntity<?> createAnnouncement(@Valid @RequestBody AnnouncementDTO announcementDTO, HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        String token = authHeader.substring(7);
        long empId = jwtUtil.getEmpIdFromToken(token);
        announcementService.createAnnouncement(announcementDTO, empId);
        return ResponseEntity.ok().body("Announcement created successfully");
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateAnnouncement(@Valid @RequestBody AnnouncementDTO announcementDTO, @PathVariable long id) {
        announcementService.updateAnnouncement(id, announcementDTO);
        return ResponseEntity.ok().body("Announcement updated successfully");
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteAnnouncement(@PathVariable long id) {
        announcementService.deleteAnnouncement(id);
        return ResponseEntity.ok().body("Announcement deleted successfully");
    }

    @GetMapping("/organization/{orgId}")
    public ResponseEntity<?> getAnnouncements(@PathVariable long orgId) {
        return ResponseEntity.ok().body(announcementService.getAnnouncements(orgId));
    }
}
