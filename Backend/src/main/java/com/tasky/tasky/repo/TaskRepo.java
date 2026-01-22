package com.tasky.tasky.repo;

import com.tasky.tasky.dto.TaskDTO;
import com.tasky.tasky.model.Task;
import com.tasky.tasky.model.TaskAssignType;
import com.tasky.tasky.model.TaskPriority;
import com.tasky.tasky.model.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepo extends JpaRepository<Task, Long> {

    List<Task> findByStatusAndOrganizationId(TaskStatus status, Long organizationId);
    List<Task> findByPriorityAndOrganizationId(TaskPriority priority, Long organizationId);
    List<Task> findByAssignTypeAndOrganizationId(TaskAssignType assignType, Long organizationId);
    List<Task> findByOrganizationId(Long organizationId);
}
