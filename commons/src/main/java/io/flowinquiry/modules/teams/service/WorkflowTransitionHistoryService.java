package io.flowinquiry.modules.teams.service;

import static io.flowinquiry.modules.teams.domain.WorkflowTransitionHistoryStatus.Completed;
import static io.flowinquiry.modules.teams.domain.WorkflowTransitionHistoryStatus.Escalated;
import static io.flowinquiry.modules.teams.domain.WorkflowTransitionHistoryStatus.In_Progress;

import io.flowinquiry.exceptions.ResourceNotFoundException;
import io.flowinquiry.modules.teams.domain.TeamRequest;
import io.flowinquiry.modules.teams.domain.WorkflowTransition;
import io.flowinquiry.modules.teams.domain.WorkflowTransitionHistory;
import io.flowinquiry.modules.teams.repository.TeamRequestRepository;
import io.flowinquiry.modules.teams.repository.WorkflowTransitionHistoryRepository;
import io.flowinquiry.modules.teams.repository.WorkflowTransitionRepository;
import io.flowinquiry.modules.teams.service.dto.TransitionItemCollectionDTO;
import io.flowinquiry.modules.teams.service.mapper.WorkflowTransitionHistoryMapper;
import jakarta.persistence.EntityNotFoundException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WorkflowTransitionHistoryService {

    private final WorkflowTransitionHistoryRepository workflowTransitionHistoryRepository;
    private final TeamRequestRepository teamRequestRepository;
    private final WorkflowTransitionRepository workflowTransitionRepository;
    private final WorkflowTransitionHistoryMapper workflowTransitionHistoryMapper;

    public WorkflowTransitionHistoryService(
            WorkflowTransitionHistoryRepository workflowTransitionHistoryRepository,
            TeamRequestRepository teamRequestRepository,
            WorkflowTransitionRepository workflowTransitionRepository,
            WorkflowTransitionHistoryMapper workflowTransitionHistoryMapper) {
        this.workflowTransitionHistoryRepository = workflowTransitionHistoryRepository;
        this.teamRequestRepository = teamRequestRepository;
        this.workflowTransitionRepository = workflowTransitionRepository;
        this.workflowTransitionHistoryMapper = workflowTransitionHistoryMapper;
    }

    /**
     * Records a state change in the workflow and persists it to WorkflowTransitionHistory.
     *
     * @param teamRequestId The ID of the team request
     * @param fromStateId The ID of the source state
     * @param toStateId The ID of the target state
     */
    @Transactional
    public void recordWorkflowTransitionHistory(
            Long teamRequestId, Long fromStateId, Long toStateId) {
        // Fetch the associated entities
        TeamRequest teamRequest =
                teamRequestRepository
                        .findById(teamRequestId)
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Team request not found: " + teamRequestId));

        WorkflowTransition transition =
                workflowTransitionRepository
                        .findByWorkflowIdAndSourceStateIdAndTargetStateId(
                                teamRequest.getWorkflow().getId(), fromStateId, toStateId)
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Transition not found for the given states"));

        // Calculate SLA due date
        Instant slaDueDate = null;
        if (transition.getSlaDuration() != null && transition.getSlaDuration() > 0) {
            slaDueDate = Instant.now().plus(transition.getSlaDuration(), ChronoUnit.HOURS);
        }

        // Create and save the WorkflowTransitionHistory entry
        WorkflowTransitionHistory history = new WorkflowTransitionHistory();
        history.setTeamRequest(teamRequest);
        history.setFromState(transition.getSourceState());
        history.setToState(transition.getTargetState());
        history.setEventName(transition.getEventName());
        history.setTransitionDate(Instant.now());
        history.setSlaDueDate(slaDueDate);
        if (transition.getTargetState().getIsFinal()) {
            history.setStatus(Completed);
        } else {
            history.setStatus(In_Progress);
        }

        workflowTransitionHistoryRepository.save(history);
    }

    public TransitionItemCollectionDTO getTransitionHistoryByTicketId(Long ticketId) {
        List<WorkflowTransitionHistory> histories =
                workflowTransitionHistoryRepository.findByTeamRequestId(ticketId);

        if (histories.isEmpty()) {
            return new TransitionItemCollectionDTO(ticketId, Collections.emptyList());
        }

        return workflowTransitionHistoryMapper.toTicketHistoryDto(ticketId, histories);
    }

    @Transactional(readOnly = true)
    public List<WorkflowTransitionHistory> getViolatingTransitions(long checkTimeInSeconds) {
        Instant checkTime = Instant.now().plusSeconds(checkTimeInSeconds);
        return workflowTransitionHistoryRepository.findViolatingTransitions(checkTime);
    }

    /**
     * Retrieves workflow transitions that have already violated their SLA.
     *
     * @return A list of violated workflow transitions.
     */
    public List<WorkflowTransitionHistory> getViolatedTransitions() {
        return workflowTransitionHistoryRepository.findViolatingTransitions(Instant.now());
    }

    /**
     * Updates the given workflow transition to 'Escalated' and persists it.
     *
     * @param transitionId The workflow transition history entry to escalate.
     */
    @Transactional
    public void escalateTransition(Long transitionId) {
        WorkflowTransitionHistory violatedTicket =
                workflowTransitionHistoryRepository
                        .findById(transitionId)
                        .orElseThrow(
                                () ->
                                        new EntityNotFoundException(
                                                "Transition not found: " + transitionId));

        violatedTicket.setStatus(Escalated);
        workflowTransitionHistoryRepository.save(violatedTicket);
    }
}
