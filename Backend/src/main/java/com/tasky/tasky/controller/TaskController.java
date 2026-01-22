package com.tasky.tasky.controller;

import com.tasky.tasky.dto.TaskDTO;
import com.tasky.tasky.security.JWTUtil;
import com.tasky.tasky.service.TaskService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/task")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private JWTUtil jwtUtil;

    @PostMapping("/create")
    public ResponseEntity<?> createTask(@Valid @RequestBody TaskDTO taskDTO, HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        String token = authHeader.substring(7);
        long empId = jwtUtil.getEmpIdFromToken(token);
        taskService.createTask(taskDTO, empId);
        return ResponseEntity.ok("Task created successfully");
    }

    @PutMapping("/update/{taskId}")
    public ResponseEntity<?> updateTask(@PathVariable Long taskId, @Valid @RequestBody TaskDTO taskDTO) {
        taskService.updateTask(taskId, taskDTO);
        return ResponseEntity.ok("Task updated successfully");
    }

    @DeleteMapping("/delete/{taskId}")
    public ResponseEntity<?> deleteTask(@PathVariable Long taskId) {
        taskService.deleteTask(taskId);
        return ResponseEntity.ok("Task deleted successfully");
    }

    @GetMapping("/get/{orgId}")
    public ResponseEntity<?> getTasks(@PathVariable Long orgId) {
        return ResponseEntity.ok(taskService.taskList(orgId));
    }

    @GetMapping("/getLowPriorityTask/{orgId}")
    public ResponseEntity<?> getTasksByLowPriority(@PathVariable Long orgId) {
        return ResponseEntity.ok(taskService.lowPriorityTasks(orgId));
    }

    @GetMapping("/getMediumPriorityTask/{orgId}")
    public ResponseEntity<?> getTasksByMediumPriority(@PathVariable Long orgId) {
        return ResponseEntity.ok(taskService.mediumPriorityTasks(orgId));
    }

    @GetMapping("/getHighPriorityTask/{orgId}")
    public ResponseEntity<?> getTasksByHighPriority(@PathVariable Long orgId) {
        return ResponseEntity.ok(taskService.highPriorityTasks(orgId));
    }

    @GetMapping("/getPendingTasks/{orgId}")
    public ResponseEntity<?> getTasksByPendingStatus(@PathVariable Long orgId) {
        return ResponseEntity.ok(taskService.taskStatusPending(orgId));
    }

    @GetMapping("/getInProgressTasks/{orgId}")
    public ResponseEntity<?> getTasksByInProgressStatus(@PathVariable Long orgId) {
        return ResponseEntity.ok(taskService.taskStatusInProgress(orgId));
    }

    @GetMapping("/getCompletedTasks/{orgId}")
    public ResponseEntity<?> getTasksByCompletedStatus(@PathVariable Long orgId) {
        return ResponseEntity.ok(taskService.taskStatusDone(orgId));
    }

    @GetMapping("/getCancelledTasks/{orgId}")
    public ResponseEntity<?> getTasksByCancelledStatus(@PathVariable Long orgId) {
        return ResponseEntity.ok(taskService.taskStatusCancelled(orgId));
    }

    @GetMapping("/getAssignedTypeTeam/{orgId}")
    public ResponseEntity<?> getAssignedTypeTeam(@PathVariable Long orgId) {
        return ResponseEntity.ok(taskService.taskAssignTypeTeam(orgId));
    }

    @GetMapping("/getAssignedTypeEmployee/{orgId}")
    public ResponseEntity<?> getAssignedTypeEmployee(@PathVariable Long orgId) {
        return ResponseEntity.ok(taskService.taskAssignTypeEmployee(orgId));
    }

}
