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
import io.flexwork.modules.teams.service.dto.WorkflowStateDTO;
import io.flexwork.modules.teams.service.dto.WorkflowTransitionDTO;
import io.flexwork.modules.teams.service.mapper.WorkflowMapper;
import io.flexwork.modules.teams.service.mapper.WorkflowStateMapper;
import io.flexwork.modules.teams.service.mapper.WorkflowTransitionMapper;
import io.flexwork.query.QueryDTO;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
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

    @Transactional
    public WorkflowDetailedDTO updateWorkflow(
            Long workflowId, WorkflowDetailedDTO updatedWorkflow) {
        Workflow existingWorkflow =
                workflowRepository
                        .findById(workflowId)
                        .orElseThrow(
                                () ->
                                        new IllegalArgumentException(
                                                "Workflow not found for id: " + workflowId));

        // Update Workflow properties
        existingWorkflow.setName(updatedWorkflow.getName());
        existingWorkflow.setRequestName(updatedWorkflow.getRequestName());
        existingWorkflow.setDescription(updatedWorkflow.getDescription());
        existingWorkflow.setVisibility(updatedWorkflow.getVisibility());

        // Save updated workflow
        workflowRepository.save(existingWorkflow);

        // Handle states
        updateStates(existingWorkflow, updatedWorkflow.getStates());

        // Handle transitions
        updateTransitions(existingWorkflow, updatedWorkflow.getTransitions());

        // Return the updated DTO
        return workflowMapper.toDetailedDto(existingWorkflow);
    }

    private void updateStates(Workflow workflow, List<WorkflowStateDTO> updatedStates) {
        List<WorkflowState> existingStates =
                workflowStateRepository.findByWorkflowId(workflow.getId());

        // Convert to maps for easy lookup
        Set<Long> updatedStateIds =
                updatedStates.stream()
                        .map(WorkflowStateDTO::getId)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet());

        // Update existing states
        existingStates.forEach(
                existingState -> {
                    if (updatedStateIds.contains(existingState.getId())) {
                        WorkflowStateDTO updatedState =
                                updatedStates.stream()
                                        .filter(
                                                state ->
                                                        state.getId().equals(existingState.getId()))
                                        .findFirst()
                                        .orElseThrow();
                        existingState.setStateName(updatedState.getStateName());
                        existingState.setIsInitial(updatedState.getIsInitial());
                        existingState.setIsFinal(updatedState.getIsFinal());
                        workflowStateRepository.save(existingState);
                    } else {
                        // Remove deleted states
                        workflowStateRepository.delete(existingState);
                    }
                });

        // Add new states
        updatedStates.stream()
                .filter(state -> state.getId() == null)
                .forEach(
                        newState -> {
                            WorkflowState state = new WorkflowState();
                            state.setWorkflow(workflow);
                            state.setStateName(newState.getStateName());
                            state.setIsInitial(newState.getIsInitial());
                            state.setIsFinal(newState.getIsFinal());
                            workflowStateRepository.save(state);
                        });
    }

    private void updateTransitions(
            Workflow workflow, List<WorkflowTransitionDTO> updatedTransitions) {
        List<WorkflowTransition> existingTransitions =
                workflowTransitionRepository.findByWorkflowId(workflow.getId());

        // Convert to maps for easy lookup
        Set<Long> updatedTransitionIds =
                updatedTransitions.stream()
                        .map(WorkflowTransitionDTO::getId)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet());

        // Update existing transitions
        existingTransitions.forEach(
                existingTransition -> {
                    if (updatedTransitionIds.contains(existingTransition.getId())) {
                        WorkflowTransitionDTO updatedTransition =
                                updatedTransitions.stream()
                                        .filter(
                                                transition ->
                                                        transition
                                                                .getId()
                                                                .equals(existingTransition.getId()))
                                        .findFirst()
                                        .orElseThrow();
                        existingTransition.setEventName(updatedTransition.getEventName());
                        existingTransition.setSourceState(
                                WorkflowState.builder()
                                        .id(updatedTransition.getSourceStateId())
                                        .build());
                        existingTransition.setTargetState(
                                WorkflowState.builder()
                                        .id(updatedTransition.getTargetStateId())
                                        .build());
                        existingTransition.setSlaDuration(updatedTransition.getSlaDuration());
                        existingTransition.setEscalateOnViolation(
                                updatedTransition.isEscalateOnViolation());
                        workflowTransitionRepository.save(existingTransition);
                    } else {
                        // Remove deleted transitions
                        workflowTransitionRepository.delete(existingTransition);
                    }
                });

        // Add new transitions
        updatedTransitions.stream()
                .filter(transition -> transition.getId() == null)
                .forEach(
                        newTransition -> {
                            WorkflowTransition transition = new WorkflowTransition();
                            transition.setWorkflow(workflow);
                            transition.setEventName(newTransition.getEventName());
                            transition.setSourceState(
                                    WorkflowState.builder()
                                            .id(newTransition.getSourceStateId())
                                            .build());
                            transition.setTargetState(
                                    WorkflowState.builder()
                                            .id(newTransition.getTargetStateId())
                                            .build());
                            transition.setSlaDuration(newTransition.getSlaDuration());
                            transition.setEscalateOnViolation(
                                    newTransition.isEscalateOnViolation());
                            workflowTransitionRepository.save(transition);
                        });
    }
}
