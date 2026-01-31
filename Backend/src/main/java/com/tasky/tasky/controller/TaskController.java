package com.tasky.tasky.controller;

import com.tasky.tasky.dto.TaskDTO;
import com.tasky.tasky.dto.UserContextDTO;
import com.tasky.tasky.security.JWTUtil;
import com.tasky.tasky.service.TaskService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/task")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private JWTUtil jwtUtil;

    @PostMapping("/create")
    public ResponseEntity<?> createTask(@Valid @RequestBody TaskDTO taskDTO, @AuthenticationPrincipal UserContextDTO userContextDTO) {
        long empId = userContextDTO.getEmpId();
        long roleId = userContextDTO.getRoleId();
        taskService.createTask(roleId, taskDTO, empId);
        return ResponseEntity.ok("Task created successfully");
    }

    @PutMapping("/update/{taskId}")
    public ResponseEntity<?> updateTask(@PathVariable Long taskId, @Valid @RequestBody TaskDTO taskDTO, @AuthenticationPrincipal UserContextDTO userContextDTO) {
        long roleId = userContextDTO.getRoleId();
        long empId = userContextDTO.getEmpId();
        taskService.updateTask(roleId, taskId, taskDTO, empId);
        return ResponseEntity.ok("Task updated successfully");
    }

    @DeleteMapping("/delete/{taskId}")
    public ResponseEntity<?> deleteTask(@PathVariable Long taskId, @AuthenticationPrincipal UserContextDTO userContextDTO) {
        long roleId = userContextDTO.getRoleId();
        taskService.deleteTask(roleId, taskId);
        return ResponseEntity.ok("Task deleted successfully");
    }

    @GetMapping("/get")
    public ResponseEntity<?> getTasks(@AuthenticationPrincipal UserContextDTO userContextDTO) {
        long orgId = userContextDTO.getOrgId();
        return ResponseEntity.ok(taskService.taskList(orgId));
    }

    @GetMapping("/getLowPriorityTask")
    public ResponseEntity<?> getTasksByLowPriority(@AuthenticationPrincipal UserContextDTO userContextDTO) {
        long orgId = userContextDTO.getOrgId();
        return ResponseEntity.ok(taskService.lowPriorityTasks(orgId));
    }

    @GetMapping("/getMediumPriorityTask")
    public ResponseEntity<?> getTasksByMediumPriority(@AuthenticationPrincipal UserContextDTO userContextDTO) {
        long orgId = userContextDTO.getOrgId();
        return ResponseEntity.ok(taskService.mediumPriorityTasks(orgId));
    }

    @GetMapping("/getHighPriorityTask")
    public ResponseEntity<?> getTasksByHighPriority(@AuthenticationPrincipal UserContextDTO userContextDTO) {
        long orgId = userContextDTO.getOrgId();
        return ResponseEntity.ok(taskService.highPriorityTasks(orgId));
    }

    @GetMapping("/getPendingTasks")
    public ResponseEntity<?> getTasksByPendingStatus(@AuthenticationPrincipal UserContextDTO userContextDTO) {
        long orgId = userContextDTO.getOrgId();
        return ResponseEntity.ok(taskService.taskStatusPending(orgId));
    }

    @GetMapping("/getInProgressTasks")
    public ResponseEntity<?> getTasksByInProgressStatus(@AuthenticationPrincipal UserContextDTO userContextDTO) {
        long orgId = userContextDTO.getOrgId();
        return ResponseEntity.ok(taskService.taskStatusInProgress(orgId));
    }

    @GetMapping("/getCompletedTasks")
    public ResponseEntity<?> getTasksByCompletedStatus(@AuthenticationPrincipal UserContextDTO userContextDTO) {
        long orgId = userContextDTO.getOrgId();
        return ResponseEntity.ok(taskService.taskStatusDone(orgId));
    }

    @GetMapping("/getCancelledTasks")
    public ResponseEntity<?> getTasksByCancelledStatus(@AuthenticationPrincipal UserContextDTO userContextDTO) {
        long orgId = userContextDTO.getOrgId();
        return ResponseEntity.ok(taskService.taskStatusCancelled(orgId));
    }

    @GetMapping("/getAssignedTypeTeam")
    public ResponseEntity<?> getAssignedTypeTeam(@AuthenticationPrincipal UserContextDTO userContextDTO) {
        long orgId = userContextDTO.getOrgId();
        return ResponseEntity.ok(taskService.taskAssignTypeTeam(orgId));
    }

    @GetMapping("/getAssignedTypeEmployee")
    public ResponseEntity<?> getAssignedTypeEmployee(@AuthenticationPrincipal UserContextDTO userContextDTO) {
        long orgId = userContextDTO.getOrgId();
        return ResponseEntity.ok(taskService.taskAssignTypeEmployee(orgId));
    }

}
