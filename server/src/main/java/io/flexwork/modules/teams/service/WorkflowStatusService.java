package io.flexwork.modules.teams.service;

import io.flexwork.modules.teams.domain.WorkflowStatus;
import io.flexwork.modules.teams.repository.WorkflowStatusRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class WorkflowStatusService {

    private final WorkflowStatusRepository workflowStatusRepository;

    public WorkflowStatusService(WorkflowStatusRepository workflowStatusRepository) {
        this.workflowStatusRepository = workflowStatusRepository;
    }

    public List<WorkflowStatus> getAllWorkflowStatuses() {
        return workflowStatusRepository.findAll();
    }

    public WorkflowStatus getWorkflowStatusById(Long id) {
        return workflowStatusRepository
                .findById(id)
                .orElseThrow(() -> new RuntimeException("Workflow status not found"));
    }

    public WorkflowStatus createWorkflowStatus(WorkflowStatus workflowStatus) {
        return workflowStatusRepository.save(workflowStatus);
    }

    public WorkflowStatus updateWorkflowStatus(Long id, WorkflowStatus updatedStatus) {
        WorkflowStatus status = getWorkflowStatusById(id);
        status.setName(updatedStatus.getName());
        status.setDescription(updatedStatus.getDescription());
        status.setOrderInWorkflow(updatedStatus.getOrderInWorkflow());
        status.setStatusPhase(updatedStatus.getStatusPhase());
        return workflowStatusRepository.save(status);
    }

    public void deleteWorkflowStatus(Long id) {
        workflowStatusRepository.deleteById(id);
    }
}
