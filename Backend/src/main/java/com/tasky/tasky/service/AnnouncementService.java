package com.tasky.tasky.service;

import com.tasky.tasky.dto.AnnouncementDTO;
import com.tasky.tasky.model.Announcement;
import com.tasky.tasky.model.Employee;
import com.tasky.tasky.repo.AnnouncementRepo;
import com.tasky.tasky.repo.EmployeeRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnnouncementService {

    @Autowired
    private AnnouncementRepo announcementRepo;

    @Autowired
    private EmployeeRepo employeeRepo;

    public void createAnnouncement(AnnouncementDTO announcementDTO, Long empId) {
        Announcement announcement = mapToEntity(announcementDTO, empId);
        try {
            announcementRepo.save(announcement);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create announcement: " + e.getMessage());
        }
    }

    public void updateAnnouncement(Long announcementId, AnnouncementDTO announcementDTO) {
        Announcement announcement = announcementRepo.findById(announcementId)
                .orElseThrow(() -> new IllegalArgumentException("Announcement not found with id: " + announcementId));

        announcement.setTitle(announcementDTO.getTitle());
        announcement.setMessage(announcementDTO.getMessage());

        announcementRepo.save(announcement);
    }

    public List<Announcement> getAnnouncements(Long orgId) {
        List<Announcement> announcements = announcementRepo.findByOrganizationId(orgId);
        return announcements;
    }

    public void deleteAnnouncement(Long announcementId) {
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
