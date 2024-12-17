package io.flowinquiry.modules.teams.service;

import io.flowinquiry.modules.teams.domain.WorkflowState;
import io.flowinquiry.modules.teams.repository.WorkflowStateRepository;
import io.flowinquiry.modules.teams.repository.WorkflowTransitionRepository;
import io.flowinquiry.modules.teams.service.dto.WorkflowStateDTO;
import io.flowinquiry.modules.teams.service.mapper.WorkflowStateMapper;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class WorkflowTransitionService {

    private final WorkflowStateRepository workflowStateRepository;
    private final WorkflowTransitionRepository workflowTransitionRepository;
    private final WorkflowStateMapper workflowStateMapper;

    public WorkflowTransitionService(
            WorkflowStateRepository workflowStateRepository,
            WorkflowTransitionRepository workflowTransitionRepository,
            WorkflowStateMapper workflowStateMapper) {
        this.workflowStateRepository = workflowStateRepository;
        this.workflowTransitionRepository = workflowTransitionRepository;
        this.workflowStateMapper = workflowStateMapper;
    }

    /**
     * Retrieves all valid target workflow states for a given workflow and current state, optionally
     * including the current state itself.
     *
     * @param workflowId the ID of the workflow
     * @param workflowStateId the ID of the current workflow state
     * @param includeSelf whether to include the current state in the results
     * @return a sorted list of WorkflowState objects
     */
    public List<WorkflowStateDTO> getValidTargetWorkflowStates(
            Long workflowId, Long workflowStateId, boolean includeSelf) {
        // Find the current state
        WorkflowState currentState =
                workflowStateRepository
                        .findById(workflowStateId)
                        .orElseThrow(
                                () ->
                                        new IllegalArgumentException(
                                                "WorkflowState not found for ID: "
                                                        + workflowStateId));

        // Find valid target states
        List<WorkflowState> targetStates =
                workflowTransitionRepository.findValidTargetStates(workflowId, workflowStateId);

        // Add current state if includeSelf is true
        if (includeSelf) {
            targetStates.add(currentState);
        }

        // Sort by isFinal, id, and stateName
        targetStates.sort(
                Comparator.comparing(WorkflowState::getIsFinal)
                        .thenComparing(WorkflowState::getId)
                        .thenComparing(WorkflowState::getStateName));
        return targetStates.stream().map(workflowStateMapper::toDto).toList();
    }
}
