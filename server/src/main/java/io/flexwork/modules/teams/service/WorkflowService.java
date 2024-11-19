package io.flexwork.modules.teams.service;

import io.flexwork.modules.teams.domain.Workflow;
import io.flexwork.modules.teams.repository.WorkflowRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WorkflowService {

    private final WorkflowRepository workflowRepository;

    public WorkflowService(WorkflowRepository workflowRepository) {
        this.workflowRepository = workflowRepository;
    }

    @Transactional
    public Workflow createWorkflow(Workflow workflow) {
        return workflowRepository.save(workflow);
    }

    @Transactional(readOnly = true)
    public List<Workflow> getAllWorkflows() {
        return workflowRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Workflow> getWorkflowById(Long id) {
        return workflowRepository.findById(id);
    }

    @Transactional
    public Workflow updateWorkflow(Long id, Workflow updatedWorkflow) {
        return workflowRepository
                .findById(id)
                .map(
                        existingWorkflow -> {
                            existingWorkflow.setName(updatedWorkflow.getName());
                            existingWorkflow.setDescription(updatedWorkflow.getDescription());
                            return workflowRepository.save(existingWorkflow);
                        })
                .orElseThrow(
                        () -> new IllegalArgumentException("Workflow not found with id: " + id));
    }

    @Transactional
    public void deleteWorkflow(Long id) {
        if (workflowRepository.existsById(id)) {
            workflowRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("Workflow not found with id: " + id);
        }
    }
}
