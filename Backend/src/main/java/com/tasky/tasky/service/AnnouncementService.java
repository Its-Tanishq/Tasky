package com.tasky.tasky.service;

import com.tasky.tasky.dto.AnnouncementDTO;
import com.tasky.tasky.model.Announcement;
import com.tasky.tasky.model.Employee;
import com.tasky.tasky.repo.AnnouncementRepo;
import com.tasky.tasky.repo.EmployeeRepo;
import com.tasky.tasky.security.RequiresPermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnnouncementService {

    @Autowired
    private AnnouncementRepo announcementRepo;

    @Autowired
    private EmployeeRepo employeeRepo;

    @RequiresPermission("CREATE_ANNOUNCEMENT")
    public void createAnnouncement(Long roleId, AnnouncementDTO announcementDTO, Long empId) {
        Announcement announcement = mapToEntity(announcementDTO, empId);
        try {
            announcementRepo.save(announcement);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create announcement: " + e.getMessage());
        }
    }

    @RequiresPermission("UPDATE_ANNOUNCEMENT")
    public void updateAnnouncement(Long roleId, Long announcementId, AnnouncementDTO announcementDTO, Long empId) {
        Announcement announcement = announcementRepo.findById(announcementId)
                .orElseThrow(() -> new IllegalArgumentException("Announcement not found with id: " + announcementId));

        Employee employee = employeeRepo.findById(empId)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found with id: " + empId));

        announcement.setTitle(announcementDTO.getTitle());
        announcement.setMessage(announcementDTO.getMessage());
        announcement.setUpdatedBy(employee.getName());

        announcementRepo.save(announcement);
    }

    @RequiresPermission("VIEW_ANNOUNCEMENT")
    public List<Announcement> getAnnouncements(Long roleId, Long orgId) {
        List<Announcement> announcements = announcementRepo.findByOrganizationId(orgId);
        return announcements;
    }

    @RequiresPermission("DELETE_ANNOUNCEMENT")
    public void deleteAnnouncement(Long roleId, Long announcementId) {
        Announcement announcement = announcementRepo.findById(announcementId)
                .orElseThrow(() -> new IllegalArgumentException("Announcement not found with id: " + announcementId));
        announcementRepo.delete(announcement);
    }

    private Announcement mapToEntity(AnnouncementDTO announcementDTO, long empId) {
        Announcement announcement = new Announcement();
        announcement.setTitle(announcementDTO.getTitle());
        announcement.setMessage(announcementDTO.getMessage());

        Employee employee = employeeRepo.findById(empId)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found with id: " + empId));
        announcement.setCreatedBy(employee.getName());
        announcement.setOrganization(employee.getOrganization());

        return announcement;
    }
}
