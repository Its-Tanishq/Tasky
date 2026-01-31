package com.tasky.tasky.service;

import com.tasky.tasky.dto.TaskDTO;
import com.tasky.tasky.exception.InvalidTaskAssignmentException;
import com.tasky.tasky.model.*;
import com.tasky.tasky.repo.EmployeeRepo;
import com.tasky.tasky.repo.TaskRepo;
import com.tasky.tasky.repo.TeamRepo;
import com.tasky.tasky.security.RequiresPermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    @Autowired
    private TaskRepo taskRepo;

    @Autowired
    private EmployeeRepo employeeRepo;

    @Autowired
    private TeamRepo teamRepo;

    @RequiresPermission("CREATE_TASK")
    public void createTask(Long roleId, TaskDTO taskDTO, Long empId) {
        Task task = mapToEntity(taskDTO, empId);
        try {
            taskRepo.save(task);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create task: " + e.getMessage());
        }
    }

    @RequiresPermission("UPDATE_TASK")
    public void updateTask(Long roleId, Long taskId, TaskDTO taskDTO, Long empId) {
        Task task = taskRepo.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found with id: " + taskId));

        Employee employee = employeeRepo.findById(empId)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found with id: " + empId));

        task.setTitle(taskDTO.getTitle());
        task.setDescription(taskDTO.getDescription());
        task.setPriority(taskDTO.getPriority());
        task.setStatus(taskDTO.getStatus());
        task.setAssignType(taskDTO.getAssignType());
        if (taskDTO.getAssignType().equals(TaskAssignType.EMPLOYEE)) {
            if (taskDTO.getAssignedEmployeeId() == null) {
                throw new InvalidTaskAssignmentException("Assigned employee ID cannot be null when assign type is EMPLOYEE");
            }
            Employee assignedEmployee = employeeRepo.findById(taskDTO.getAssignedEmployeeId())
                    .orElseThrow(() -> new IllegalArgumentException("Employee not found with id: " + taskDTO.getAssignedEmployeeId()));
            task.setAssignedEmployee(assignedEmployee);
            task.setAssignedTeam(null);
        } else if (taskDTO.getAssignType().equals(TaskAssignType.TEAM)) {
            if (taskDTO.getAssignedTeamId() == null) {
                throw new InvalidTaskAssignmentException("Assigned team ID cannot be null when assign type is TEAM");
            }
            Team team = teamRepo.findById(taskDTO.getAssignedTeamId())
                    .orElseThrow(() -> new IllegalArgumentException("Team not found with id: " + taskDTO.getAssignedTeamId()));
            task.setAssignedTeam(team);
            task.setAssignedEmployee(null);
        } else {
            throw new IllegalArgumentException("Invalid task assign type: " + taskDTO.getAssignType());
        }
        task.setDueDate(taskDTO.getDueDate());
        task.setUpdatedBy(employee.getName());

        taskRepo.save(task);
    }

    @RequiresPermission("DELETE_TASK")
    public void deleteTask(Long roleId, Long taskId) {
        Task task = taskRepo.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found with id: " + taskId));
        taskRepo.delete(task);
    }

    public List<Task> taskList(Long orgId) {
        List<Task> tasks = taskRepo.findByOrganizationId(orgId);
        return tasks;
    }

    public List<Task> lowPriorityTasks(Long orgId) {
        List<Task> tasks = taskRepo.findByPriorityAndOrganizationId(TaskPriority.LOW, orgId);
        return tasks;
    }

    public List<Task> mediumPriorityTasks(Long orgId) {
        List<Task> tasks = taskRepo.findByPriorityAndOrganizationId(TaskPriority.MEDIUM, orgId);
        return tasks;
    }

    public List<Task> highPriorityTasks(Long orgId) {
        List<Task> tasks = taskRepo.findByPriorityAndOrganizationId(TaskPriority.HIGH, orgId);
        return tasks;
    }

    public List<Task> taskStatusPending(Long orgId) {
        List<Task> tasks = taskRepo.findByStatusAndOrganizationId(TaskStatus.PENDING, orgId);
        return tasks;
    }

    public List<Task> taskStatusInProgress(Long orgId) {
        List<Task> tasks = taskRepo.findByStatusAndOrganizationId(TaskStatus.IN_PROGRESS, orgId);
        return tasks;
    }

    public List<Task> taskStatusCancelled(Long orgId) {
        List<Task> tasks = taskRepo.findByStatusAndOrganizationId(TaskStatus.CANCELLED, orgId);
        return tasks;
    }

    public List<Task> taskStatusDone(Long orgId) {
        List<Task> tasks = taskRepo.findByStatusAndOrganizationId(TaskStatus.DONE, orgId);
        return tasks;
    }

    public List<Task> taskAssignTypeTeam(Long orgId) {
        List<Task> tasks = taskRepo.findByAssignTypeAndOrganizationId(TaskAssignType.TEAM, orgId);
        return tasks;
    }

    public List<Task> taskAssignTypeEmployee(Long orgId) {
        List<Task> tasks = taskRepo.findByAssignTypeAndOrganizationId(TaskAssignType.EMPLOYEE, orgId);
        return tasks;
    }

    private Task mapToEntity(TaskDTO taskDTO, Long empId) {
        Task task = new Task();
        Employee employee = employeeRepo.findById(empId)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found with id: " + empId));
        task.setOrganization(employee.getOrganization());
        task.setTitle(taskDTO.getTitle());
        task.setDescription(taskDTO.getDescription());
        task.setPriority(taskDTO.getPriority());
        task.setAssignType(taskDTO.getAssignType());
        if (taskDTO.getAssignType().equals(TaskAssignType.EMPLOYEE)) {
            if (taskDTO.getAssignedEmployeeId() == null) {
                throw new InvalidTaskAssignmentException("Assigned employee ID cannot be null when assign type is EMPLOYEE");
            }
            Employee assignedEmployee = employeeRepo.findById(taskDTO.getAssignedEmployeeId())
                    .orElseThrow(() -> new IllegalArgumentException("Employee not found with id: " + taskDTO.getAssignedEmployeeId()));
            task.setAssignedEmployee(assignedEmployee);
        } else if (taskDTO.getAssignType().equals(TaskAssignType.TEAM)) {
            if (taskDTO.getAssignedTeamId() == null) {
                throw new InvalidTaskAssignmentException("Assigned team ID cannot be null when assign type is TEAM");
            }
            Team team = teamRepo.findById(taskDTO.getAssignedTeamId())
                    .orElseThrow(() -> new IllegalArgumentException("Team not found with id: " + taskDTO.getAssignedTeamId()));
            task.setAssignedTeam(team);
        } else {
            throw new IllegalArgumentException("Invalid task assign type: " + taskDTO.getAssignType());
        }
        task.setDueDate(taskDTO.getDueDate());
        task.setCreatedBy(employee.getName());

        return task;
    }
}
