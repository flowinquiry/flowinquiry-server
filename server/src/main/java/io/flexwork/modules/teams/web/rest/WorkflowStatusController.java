package io.flexwork.modules.teams.web.rest;

import io.flexwork.modules.teams.domain.WorkflowStatus;
import io.flexwork.modules.teams.service.WorkflowStatusService;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/teams/workflow-statuses")
public class WorkflowStatusController {

    private final WorkflowStatusService workflowStatusService;

    public WorkflowStatusController(WorkflowStatusService workflowStatusService) {
        this.workflowStatusService = workflowStatusService;
    }

    @GetMapping
    public List<WorkflowStatus> getAllWorkflowStatuses() {
        return workflowStatusService.getAllWorkflowStatuses();
    }

    @GetMapping("/{id}")
    public WorkflowStatus getWorkflowStatusById(@PathVariable Long id) {
        return workflowStatusService.getWorkflowStatusById(id);
    }

    @PostMapping
    public WorkflowStatus createWorkflowStatus(@RequestBody WorkflowStatus workflowStatus) {
        return workflowStatusService.createWorkflowStatus(workflowStatus);
    }

    @PutMapping("/{id}")
    public WorkflowStatus updateWorkflowStatus(
            @PathVariable Long id, @RequestBody WorkflowStatus updatedStatus) {
        return workflowStatusService.updateWorkflowStatus(id, updatedStatus);
    }

    @DeleteMapping("/{id}")
    public void deleteWorkflowStatus(@PathVariable Long id) {
        workflowStatusService.deleteWorkflowStatus(id);
    }
}
