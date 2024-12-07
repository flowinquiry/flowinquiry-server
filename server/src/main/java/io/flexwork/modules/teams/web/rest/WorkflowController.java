package io.flexwork.modules.teams.web.rest;

import io.flexwork.modules.teams.domain.Workflow;
import io.flexwork.modules.teams.service.WorkflowService;
import io.flexwork.modules.teams.service.WorkflowTransitionService;
import io.flexwork.modules.teams.service.dto.WorkflowDTO;
import io.flexwork.modules.teams.service.dto.WorkflowDetailedDTO;
import io.flexwork.modules.teams.service.dto.WorkflowStateDTO;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/workflows")
public class WorkflowController {

    private final WorkflowService workflowService;

    private final WorkflowTransitionService workflowTransitionService;

    public WorkflowController(
            WorkflowService workflowService, WorkflowTransitionService workflowTransitionService) {
        this.workflowService = workflowService;
        this.workflowTransitionService = workflowTransitionService;
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

    /**
     * Endpoint to retrieve all valid target states for a given workflow and current state ID, with
     * an option to include the current state itself.
     *
     * @param workflowId the ID of the workflow
     * @param workflowStateId the ID of the current workflow state
     * @param includeSelf whether to include the current state in the results
     * @return a list of valid target WorkflowState objects
     */
    @GetMapping("/{workflowId}/transitions")
    public ResponseEntity<List<WorkflowStateDTO>> getValidTargetStates(
            @PathVariable Long workflowId,
            @RequestParam Long workflowStateId,
            @RequestParam(defaultValue = "false") boolean includeSelf) {
        List<WorkflowStateDTO> validTargetStates =
                workflowTransitionService.getValidTargetWorkflowStates(
                        workflowId, workflowStateId, includeSelf);
        return ResponseEntity.ok(validTargetStates);
    }

    /**
     * Get workflow details including states and transitions.
     *
     * @param workflowId The ID of the workflow to retrieve.
     * @return WorkflowDetailedDTO if found, otherwise 404.
     */
    @GetMapping("/{workflowId}/details")
    public ResponseEntity<WorkflowDetailedDTO> getWorkflowDetail(@PathVariable Long workflowId) {
        return workflowService
                .getWorkflowDetail(workflowId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/details")
    public ResponseEntity<WorkflowDetailedDTO> saveWorkflow(
            @RequestBody WorkflowDetailedDTO workflowDetailedDTO) {
        WorkflowDetailedDTO savedWorkflow = workflowService.saveWorkflow(workflowDetailedDTO);
        return ResponseEntity.ok(savedWorkflow);
    }
}
