package io.flexwork.modules.teams.web.rest;

import io.flexwork.modules.teams.domain.Workflow;
import io.flexwork.modules.teams.service.WorkflowService;
import io.flexwork.modules.teams.service.dto.WorkflowDTO;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/workflows")
public class WorkflowController {

    private final WorkflowService workflowService;

    public WorkflowController(WorkflowService workflowService) {
        this.workflowService = workflowService;
    }

    @PostMapping
    public ResponseEntity<Workflow> createWorkflow(@RequestBody Workflow workflow) {
        Workflow createdWorkflow = workflowService.createWorkflow(workflow);
        return ResponseEntity.ok(createdWorkflow);
    }

    @GetMapping
    public ResponseEntity<List<Workflow>> getAllWorkflows() {
        List<Workflow> workflows = workflowService.getAllWorkflows();
        return ResponseEntity.ok(workflows);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Workflow> getWorkflowById(@PathVariable Long id) {
        return workflowService
                .getWorkflowById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Workflow> updateWorkflow(
            @PathVariable Long id, @RequestBody Workflow workflow) {
        try {
            Workflow updatedWorkflow = workflowService.updateWorkflow(id, workflow);
            return ResponseEntity.ok(updatedWorkflow);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWorkflow(@PathVariable Long id) {
        try {
            workflowService.deleteWorkflow(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get all workflows associated with a team.
     *
     * @param teamId the ID of the team.
     * @return a list of workflows available for the team.
     */
    @GetMapping("/teams/{teamId}")
    public ResponseEntity<List<WorkflowDTO>> getWorkflowsByTeam(@PathVariable Long teamId) {
        List<WorkflowDTO> workflows = workflowService.getWorkflowsForTeam(teamId);
        return ResponseEntity.ok(workflows);
    }
}
