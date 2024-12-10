package io.flexwork.modules.teams.web.rest;

import io.flexwork.modules.teams.domain.Workflow;
import io.flexwork.modules.teams.service.WorkflowService;
import io.flexwork.modules.teams.service.WorkflowTransitionService;
import io.flexwork.modules.teams.service.dto.WorkflowDTO;
import io.flexwork.modules.teams.service.dto.WorkflowDetailedDTO;
import io.flexwork.modules.teams.service.dto.WorkflowStateDTO;
import io.flexwork.query.QueryDTO;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<WorkflowDTO> createWorkflow(@RequestBody Workflow workflow) {
        WorkflowDTO createdWorkflow = workflowService.createWorkflow(workflow);
        return ResponseEntity.ok(createdWorkflow);
    }

    @PostMapping("/search")
    public ResponseEntity<Page<WorkflowDTO>> findWorkflows(
            @Valid @RequestBody Optional<QueryDTO> queryDTO, Pageable pageable) {
        return new ResponseEntity<>(
                workflowService.findWorkflows(queryDTO, pageable), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Workflow> getWorkflowById(@PathVariable Long id) {
        return workflowService
                .getWorkflowById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<WorkflowDTO> updateWorkflow(
            @PathVariable Long id, @RequestBody WorkflowDTO workflow) {
        try {
            WorkflowDTO updatedWorkflow = workflowService.updateWorkflow(id, workflow);
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
     * Get global workflows not linked to a specific team.
     *
     * @param teamId the ID of the team
     * @return List of WorkflowDTOs representing global workflows not linked to the team
     */
    @GetMapping("/teams/{teamId}/global-workflows-not-linked-yet")
    public ResponseEntity<List<WorkflowDTO>> getGlobalWorkflowsNotLinkedToTeam(
            @RequestParam Long teamId) {
        List<WorkflowDTO> workflows = workflowService.listGlobalWorkflowsNotLinkedToTeam(teamId);
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
    @GetMapping("/details/{workflowId}")
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

    @PutMapping("/details/{workflowId}")
    public ResponseEntity<WorkflowDetailedDTO> updateWorkflow(
            @PathVariable Long workflowId,
            @Valid @RequestBody WorkflowDetailedDTO workflowDetailedDTO) {
        WorkflowDetailedDTO updatedWorkflow =
                workflowService.updateWorkflow(workflowId, workflowDetailedDTO);

        return ResponseEntity.ok(updatedWorkflow);
    }
}
