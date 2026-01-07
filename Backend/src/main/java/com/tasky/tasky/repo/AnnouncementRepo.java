package com.tasky.tasky.repo;

import com.tasky.tasky.dto.AnnouncementDTO;
import com.tasky.tasky.model.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnnouncementRepo extends JpaRepository<Announcement, Long> {

    List<AnnouncementDTO> findByCreatedBy(String createdBy);
}