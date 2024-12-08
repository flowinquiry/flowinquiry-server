package io.flexwork.modules.teams.service;

import static io.flexwork.query.QueryUtils.createSpecification;

import io.flexwork.modules.teams.domain.Workflow;
import io.flexwork.modules.teams.domain.WorkflowState;
import io.flexwork.modules.teams.domain.WorkflowTransition;
import io.flexwork.modules.teams.repository.WorkflowRepository;
import io.flexwork.modules.teams.repository.WorkflowStateRepository;
import io.flexwork.modules.teams.repository.WorkflowTransitionRepository;
import io.flexwork.modules.teams.service.dto.WorkflowDTO;
import io.flexwork.modules.teams.service.dto.WorkflowDetailedDTO;
import io.flexwork.modules.teams.service.mapper.WorkflowMapper;
import io.flexwork.modules.teams.service.mapper.WorkflowStateMapper;
import io.flexwork.modules.teams.service.mapper.WorkflowTransitionMapper;
import io.flexwork.query.QueryDTO;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WorkflowService {

    private final WorkflowRepository workflowRepository;

    private final WorkflowStateRepository workflowStateRepository;

    private final WorkflowTransitionRepository workflowTransitionRepository;

    private final WorkflowMapper workflowMapper;

    private final WorkflowStateMapper workflowStateMapper;

    private final WorkflowTransitionMapper workflowTransitionMapper;

    public WorkflowService(
            WorkflowRepository workflowRepository,
            WorkflowStateRepository workflowStateRepository,
            WorkflowTransitionRepository workflowTransitionRepository,
            WorkflowMapper workflowMapper,
            WorkflowStateMapper workflowStateMapper,
            WorkflowTransitionMapper workflowTransitionMapper) {
        this.workflowRepository = workflowRepository;
        this.workflowStateRepository = workflowStateRepository;
        this.workflowTransitionRepository = workflowTransitionRepository;
        this.workflowMapper = workflowMapper;
        this.workflowStateMapper = workflowStateMapper;
        this.workflowTransitionMapper = workflowTransitionMapper;
    }

    @Transactional
    public WorkflowDTO createWorkflow(Workflow workflow) {
        return workflowMapper.toDto(workflowRepository.save(workflow));
    }

    @Transactional(readOnly = true)
    public Optional<Workflow> getWorkflowById(Long id) {
        return workflowRepository.findById(id);
    }

    @Transactional
    public WorkflowDTO updateWorkflow(Long id, WorkflowDTO updatedWorkflow) {
        return workflowRepository
                .findById(id)
                .map(
                        existingWorkflow -> {
                            workflowMapper.updateEntity(updatedWorkflow, existingWorkflow);
                            return workflowMapper.toDto(workflowRepository.save(existingWorkflow));
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

    public Optional<WorkflowDetailedDTO> getWorkflowDetail(Long workflowId) {
        return workflowRepository
                .findWithDetailsById(workflowId)
                .map(workflowMapper::toDetailedDto);
    }

    @Transactional(readOnly = true)
    public Page<WorkflowDTO> findWorkflows(Optional<QueryDTO> queryDTO, Pageable pageable) {
        Specification<Workflow> spec = createSpecification(queryDTO);
        return workflowRepository.findAll(spec, pageable).map(workflowMapper::toDto);
    }

    @Transactional
    public WorkflowDetailedDTO saveWorkflow(WorkflowDetailedDTO dto) {
        Workflow workflow = workflowMapper.toEntity(dto);
        Workflow savedWorkflow = workflowRepository.save(workflow);

        // Save states
        List<WorkflowState> states =
                dto.getStates().stream()
                        .map(
                                stateDto -> {
                                    WorkflowState state = workflowStateMapper.toEntity(stateDto);
                                    state.setWorkflow(savedWorkflow);
                                    return state;
                                })
                        .collect(Collectors.toList());
        workflowStateRepository.saveAll(states);

        // Save transitions
        List<WorkflowTransition> transitions =
                dto.getTransitions().stream()
                        .map(
                                transitionDto -> {
                                    WorkflowTransition transition =
                                            workflowTransitionMapper.toEntity(transitionDto);
                                    transition.setWorkflow(savedWorkflow);
                                    transition.setSourceState(
                                            states.stream()
                                                    .filter(
                                                            state ->
                                                                    state.getId()
                                                                            .equals(
                                                                                    transitionDto
                                                                                            .getSourceStateId()))
                                                    .findFirst()
                                                    .orElse(null));
                                    transition.setTargetState(
                                            states.stream()
                                                    .filter(
                                                            state ->
                                                                    state.getId()
                                                                            .equals(
                                                                                    transitionDto
                                                                                            .getTargetStateId()))
                                                    .findFirst()
                                                    .orElse(null));
                                    return transition;
                                })
                        .collect(Collectors.toList());
        workflowTransitionRepository.saveAll(transitions);

        return workflowMapper.toDetailedDto(workflow);
    }
}
