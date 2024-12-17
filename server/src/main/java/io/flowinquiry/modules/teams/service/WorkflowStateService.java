package io.flowinquiry.modules.teams.service;

import io.flowinquiry.modules.teams.repository.WorkflowStateRepository;
import io.flowinquiry.modules.teams.service.mapper.WorkflowStateMapper;
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
}
