package io.flexwork.modules.teams.service;

import io.flexwork.modules.teams.domain.Workflow;
import io.flexwork.modules.teams.repository.WorkflowRepository;
import io.flexwork.modules.teams.service.dto.WorkflowDTO;
import io.flexwork.modules.teams.service.mapper.WorkflowMapper;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WorkflowService {

    private final WorkflowRepository workflowRepository;

    private final WorkflowMapper workflowMapper;

    public WorkflowService(WorkflowRepository workflowRepository, WorkflowMapper workflowMapper) {
        this.workflowRepository = workflowRepository;
        this.workflowMapper = workflowMapper;
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

    /**
     * Fetch all workflows associated with a team.
     *
     * @param teamId the ID of the team.
     * @return a list of workflows available for the team.
     */
    public List<WorkflowDTO> getWorkflowsForTeam(Long teamId) {
        return workflowRepository.findAllWorkflowsByTeam(teamId).stream()
                .map(workflowMapper::toDto)
                .toList();
    }
}
