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
import jakarta.persistence.EntityNotFoundException;
import java.util.*;
import java.util.function.Function;
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
            Long workflowId, WorkflowDetailedDTO updatedWorkflowDTO) {
        // Step 1: Save the workflow details
        Workflow workflowEntity =
                workflowRepository
                        .findById(workflowId)
                        .orElseThrow(
                                () ->
                                        new EntityNotFoundException(
                                                "Workflow not found with id: " + workflowId));
        workflowMapper.updateEntity(updatedWorkflowDTO, workflowEntity);
        Workflow savedWorkflowEntity = workflowRepository.save(workflowEntity);

        // Step 2: Handle states
        List<WorkflowState> existingStates = workflowStateRepository.findByWorkflowId(workflowId);
        Map<Long, WorkflowState> existingStateMap =
                existingStates.stream()
                        .collect(Collectors.toMap(WorkflowState::getId, Function.identity()));

        // Identify states to delete
        List<Long> stateIdsInDto =
                updatedWorkflowDTO.getStates().stream()
                        .map(WorkflowStateDTO::getId)
                        .filter(Objects::nonNull)
                        .toList();

        List<WorkflowState> statesToDelete =
                existingStates.stream()
                        .filter(state -> !stateIdsInDto.contains(state.getId()))
                        .toList();

        // Delete transitions associated with deleted states
        List<Long> deletedStateIds = statesToDelete.stream().map(WorkflowState::getId).toList();
        if (!deletedStateIds.isEmpty()) {
            workflowTransitionRepository.deleteBySourceStateIdInOrTargetStateIdIn(
                    deletedStateIds, deletedStateIds);
        }

        // Delete the states
        workflowStateRepository.deleteAll(statesToDelete);

        // Save or update states
        Map<Long, WorkflowState> stateMappingByClientId = new HashMap<>();
        List<WorkflowState> newStates =
                updatedWorkflowDTO.getStates().stream()
                        .map(
                                stateDTO -> {
                                    WorkflowState stateEntity =
                                            existingStateMap.get(stateDTO.getId());
                                    if (stateEntity == null) {
                                        // New state
                                        stateEntity = workflowStateMapper.toEntity(stateDTO);
                                        stateEntity.setWorkflow(savedWorkflowEntity);
                                        stateMappingByClientId.put(
                                                stateDTO.getId(),
                                                stateEntity); // Track mapping using client-provided
                                        // ID
                                    } else {
                                        // Update existing state
                                        workflowStateMapper.updateEntity(stateDTO, stateEntity);
                                        stateMappingByClientId.put(
                                                stateDTO.getId(),
                                                stateEntity); // Map existing IDs as well
                                    }
                                    return stateEntity;
                                })
                        .toList();

        // Save all states
        newStates = workflowStateRepository.saveAll(newStates);

        // Update the stateMappingByClientId with the saved database IDs for new states
        newStates.forEach(
                savedState -> {
                    updatedWorkflowDTO.getStates().stream()
                            .filter(
                                    stateDTO ->
                                            savedState
                                                    .getStateName()
                                                    .equals(stateDTO.getStateName()))
                            .map(WorkflowStateDTO::getId)
                            .findFirst()
                            .ifPresent(
                                    clientId -> stateMappingByClientId.put(clientId, savedState));
                });

        // Step 3: Handle transitions
        List<WorkflowTransition> existingTransitions =
                workflowTransitionRepository.findByWorkflowId(workflowId);
        Map<Long, WorkflowTransition> existingTransitionMap =
                existingTransitions.stream()
                        .collect(Collectors.toMap(WorkflowTransition::getId, Function.identity()));

        // Identify transitions to delete
        List<Long> transitionIdsInDto =
                updatedWorkflowDTO.getTransitions().stream()
                        .map(WorkflowTransitionDTO::getId)
                        .filter(Objects::nonNull)
                        .toList();

        List<WorkflowTransition> transitionsToDelete =
                existingTransitions.stream()
                        .filter(transition -> !transitionIdsInDto.contains(transition.getId()))
                        .toList();

        // Delete the transitions
        workflowTransitionRepository.deleteAll(transitionsToDelete);

        // Save or update transitions
        List<WorkflowTransition> newTransitions =
                updatedWorkflowDTO.getTransitions().stream()
                        .map(
                                transitionDTO -> {
                                    WorkflowTransition transitionEntity =
                                            existingTransitionMap.get(transitionDTO.getId());
                                    if (transitionEntity == null) {
                                        // New transition
                                        transitionEntity =
                                                workflowTransitionMapper.toEntity(transitionDTO);
                                        transitionEntity.setWorkflow(savedWorkflowEntity);
                                    } else {
                                        // Update existing transition
                                        workflowTransitionMapper.updateEntity(
                                                transitionDTO, transitionEntity);
                                    }

                                    // Update source and target state references
                                    if (transitionDTO.getSourceStateId() != null) {
                                        transitionEntity.setSourceState(
                                                stateMappingByClientId.get(
                                                        transitionDTO.getSourceStateId()));
                                    }
                                    if (transitionDTO.getTargetStateId() != null) {
                                        transitionEntity.setTargetState(
                                                stateMappingByClientId.get(
                                                        transitionDTO.getTargetStateId()));
                                    }

                                    return transitionEntity;
                                })
                        .toList();
        newTransitions = workflowTransitionRepository.saveAll(newTransitions);

        // Step 4: Return the updated workflow
        WorkflowDetailedDTO updatedWorkflow = workflowMapper.toDetailedDto(workflowEntity);
        updatedWorkflow.setStates(newStates.stream().map(workflowStateMapper::toDto).toList());
        updatedWorkflow.setTransitions(
                newTransitions.stream().map(workflowTransitionMapper::toDto).toList());

        return updatedWorkflow;
    }

    /**
     * List global workflows not linked to the given team.
     *
     * @param teamId the ID of the team
     * @return List of WorkflowDTOs representing the global workflows
     */
    public List<WorkflowDTO> listGlobalWorkflowsNotLinkedToTeam(Long teamId) {
        return workflowRepository.findGlobalWorkflowsNotLinkedToTeam(teamId).stream()
                .map(workflowMapper::toDto)
                .toList();
    }
}
