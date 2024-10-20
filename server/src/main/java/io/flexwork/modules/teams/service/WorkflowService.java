package io.flexwork.modules.teams.service;

import io.flexwork.modules.teams.domain.Workflow;
import io.flexwork.modules.teams.repository.WorkflowRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class WorkflowService {

    private final WorkflowRepository workflowRepository;

    public WorkflowService(WorkflowRepository workflowRepository) {
        this.workflowRepository = workflowRepository;
    }

    public List<Workflow> getAllWorkflows() {
        return workflowRepository.findAll();
    }

    public Workflow getWorkflowById(Long id) {
        return workflowRepository
                .findById(id)
                .orElseThrow(() -> new RuntimeException("Workflow not found"));
    }

    public Workflow createWorkflow(Workflow workflow) {
        return workflowRepository.save(workflow);
    }

    public Workflow updateWorkflow(Long id, Workflow updatedWorkflow) {
        Workflow workflow = getWorkflowById(id);
        workflow.setName(updatedWorkflow.getName());
        workflow.setDescription(updatedWorkflow.getDescription());
        workflow.setUpdatedAt(updatedWorkflow.getUpdatedAt());
        return workflowRepository.save(workflow);
    }

    public void deleteWorkflow(Long id) {
        workflowRepository.deleteById(id);
    }
}
