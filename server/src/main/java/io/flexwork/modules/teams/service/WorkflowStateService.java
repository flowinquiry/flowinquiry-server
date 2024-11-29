package io.flexwork.modules.teams.service;

import io.flexwork.modules.teams.repository.WorkflowStateRepository;
import io.flexwork.modules.teams.service.dto.WorkflowStateDTO;
import io.flexwork.modules.teams.service.mapper.WorkflowStateMapper;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class WorkflowStateService {

    private final WorkflowStateRepository workflowStateRepository;

    private final WorkflowStateMapper workflowStateMapper;

    public WorkflowStateService(
            WorkflowStateRepository workflowStateRepository,
            WorkflowStateMapper workflowStateMapper) {
        this.workflowStateRepository = workflowStateRepository;
        this.workflowStateMapper = workflowStateMapper;
    }

    public List<WorkflowStateDTO> getStatesByWorkflowId(Long workflowId) {
        return workflowStateRepository.findByWorkflowId(workflowId).stream()
                .map(workflowStateMapper::toDto)
                .toList();
    }

    public List<WorkflowStateDTO> getStatesExcludingState(
            Long workflowId, String excludedStateName) {
        return workflowStateRepository
                .findStatesByWorkflowExcludingState(workflowId, excludedStateName)
                .stream()
                .map(workflowStateMapper::toDto)
                .toList();
    }
}
