package io.flexwork.modules.teams.service;

import static io.flexwork.modules.teams.domain.WorkflowTransitionHistoryStatus.Completed;
import static io.flexwork.query.QueryUtils.createSpecification;

import io.flexwork.modules.audit.AuditLogUpdateEvent;
import io.flexwork.modules.teams.domain.TeamRequest;
import io.flexwork.modules.teams.domain.WorkflowState;
import io.flexwork.modules.teams.domain.WorkflowTransition;
import io.flexwork.modules.teams.domain.WorkflowTransitionHistory;
import io.flexwork.modules.teams.domain.WorkflowTransitionHistoryStatus;
import io.flexwork.modules.teams.repository.TeamRequestRepository;
import io.flexwork.modules.teams.repository.WorkflowStateRepository;
import io.flexwork.modules.teams.repository.WorkflowTransitionHistoryRepository;
import io.flexwork.modules.teams.repository.WorkflowTransitionRepository;
import io.flexwork.modules.teams.service.dto.PriorityDistributionDTO;
import io.flexwork.modules.teams.service.dto.TeamRequestDTO;
import io.flexwork.modules.teams.service.dto.TicketActionCountByDateDTO;
import io.flexwork.modules.teams.service.dto.TicketDistributionDTO;
import io.flexwork.modules.teams.service.event.NewTeamRequestCreatedEvent;
import io.flexwork.modules.teams.service.event.TeamRequestWorkStateTransitionEvent;
import io.flexwork.modules.teams.service.mapper.TeamRequestMapper;
import io.flexwork.modules.usermanagement.service.dto.TicketStatisticsDTO;
import io.flexwork.query.GroupFilter;
import io.flexwork.query.QueryDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.jclouds.rest.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TeamRequestService {
    @PersistenceContext private EntityManager entityManager;

    private final TeamRequestRepository teamRequestRepository;
    private final TeamRequestMapper teamRequestMapper;
    private final WorkflowStateRepository workflowStateRepository;
    private final WorkflowTransitionRepository workflowTransitionRepository;
    private final WorkflowTransitionHistoryRepository workflowTransitionHistoryRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public TeamRequestService(
            TeamRequestRepository teamRequestRepository,
            TeamRequestMapper teamRequestMapper,
            WorkflowTransitionRepository workflowTransitionRepository,
            WorkflowStateRepository workflowStateRepository,
            WorkflowTransitionHistoryRepository workflowTransitionHistoryRepository,
            ApplicationEventPublisher eventPublisher) {
        this.teamRequestRepository = teamRequestRepository;
        this.teamRequestMapper = teamRequestMapper;
        this.workflowTransitionRepository = workflowTransitionRepository;
        this.workflowStateRepository = workflowStateRepository;
        this.workflowTransitionHistoryRepository = workflowTransitionHistoryRepository;
        this.eventPublisher = eventPublisher;
    }

    public Page<TeamRequestDTO> findTeamRequests(QueryDTO queryDTO, Pageable pageable) {
        if (!hasTeamIdFilter(queryDTO)) {
            throw new ResourceNotFoundException("No team id found");
        }
        Specification<TeamRequest> spec = createSpecification(Optional.of(queryDTO));
        return teamRequestRepository.findAll(spec, pageable).map(teamRequestMapper::toDto);
    }

    @Transactional(readOnly = true)
    public TeamRequestDTO getTeamRequestById(Long id) {
        TeamRequest teamRequest =
                teamRequestRepository
                        .findById(id)
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "TeamRequest not found with id: " + id));
        return teamRequestMapper.toDto(teamRequest);
    }

    @Transactional
    public TeamRequestDTO createTeamRequest(TeamRequestDTO teamRequestDTO) {

        Long workflowId = teamRequestDTO.getWorkflowId();
        if (workflowId == null) {
            throw new ResourceNotFoundException("No workflow id found");
        }
        WorkflowState initialStateByWorkflowId =
                workflowStateRepository.findInitialStateByWorkflowId(workflowId);
        if (initialStateByWorkflowId == null) {
            throw new ResourceNotFoundException(
                    "No initial state found for workflow id " + workflowId);
        }
        teamRequestDTO.setIsNew(true);
        teamRequestDTO.setCurrentStateId(initialStateByWorkflowId.getId());
        teamRequestDTO.setIsCompleted(false);

        TeamRequest teamRequest = teamRequestMapper.toEntity(teamRequestDTO);
        teamRequest = teamRequestRepository.save(teamRequest);
        // Clear the persistence context to force a reload
        entityManager.clear();

        ZonedDateTime slaDueDate =
                calculateEarliestSlaDueDate(
                        teamRequest.getWorkflow().getId(), initialStateByWorkflowId.getId());

        WorkflowTransitionHistory history = new WorkflowTransitionHistory();
        history.setTeamRequest(teamRequest);
        history.setFromState(null);
        history.setToState(initialStateByWorkflowId);
        history.setEventName("Created");
        history.setTransitionDate(ZonedDateTime.now());
        history.setSlaDueDate(slaDueDate);
        history.setStatus(WorkflowTransitionHistoryStatus.In_Progress);
        workflowTransitionHistoryRepository.save(history);

        teamRequest =
                teamRequestRepository
                        .findById(teamRequest.getId())
                        .orElseThrow(() -> new EntityNotFoundException("TeamRequest not found"));
        TeamRequestDTO savedTeamRequestDTO = teamRequestMapper.toDto(teamRequest);
        eventPublisher.publishEvent(new NewTeamRequestCreatedEvent(this, savedTeamRequestDTO));
        return savedTeamRequestDTO;
    }

    @Transactional
    public TeamRequestDTO updateTeamRequest(TeamRequestDTO teamRequestDTO) {

        TeamRequest existingTeamRequest =
                teamRequestRepository
                        .findById(teamRequestDTO.getId())
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "TeamRequest not found with id: "
                                                        + teamRequestDTO.getId()));
        TeamRequestDTO previousTeamRequest = teamRequestMapper.toDto(existingTeamRequest);
        Long previousState = previousTeamRequest.getCurrentStateId();

        teamRequestMapper.updateEntity(teamRequestDTO, existingTeamRequest);

        boolean isStateChanged =
                (!Objects.equals(previousState, teamRequestDTO.getCurrentStateId()));
        existingTeamRequest.setIsNew(!isStateChanged);
        if (isStateChanged) {
            boolean finalState =
                    workflowStateRepository.isFinalState(
                            teamRequestDTO.getWorkflowId(), teamRequestDTO.getCurrentStateId());
            existingTeamRequest.setIsCompleted(finalState);
            if (teamRequestDTO.getActualCompletionDate() == null) {
                existingTeamRequest.setActualCompletionDate(LocalDate.now());
            }
        }

        TeamRequestDTO savedTeamRequest =
                teamRequestMapper.toDto(teamRequestRepository.save(existingTeamRequest));
        eventPublisher.publishEvent(
                new AuditLogUpdateEvent(this, previousTeamRequest, teamRequestDTO));

        Long currentState = savedTeamRequest.getCurrentStateId();
        if (!Objects.equals(previousState, currentState)) {
            eventPublisher.publishEvent(
                    new TeamRequestWorkStateTransitionEvent(
                            this, teamRequestDTO.getId(), previousState, currentState));
        }
        return savedTeamRequest;
    }

    @Transactional
    public void deleteTeamRequest(Long id) {
        if (!teamRequestRepository.existsById(id)) {
            throw new ResourceNotFoundException("TeamRequest not found with id: " + id);
        }
        teamRequestRepository.deleteById(id);
    }

    private static boolean hasTeamIdFilter(QueryDTO queryDTO) {
        if (queryDTO == null || queryDTO.getGroups() == null) {
            return false;
        }

        // Check for "team.id" in filters at the top level
        if (queryDTO.getFilters() != null) {
            boolean hasTeamIdInFilters =
                    queryDTO.getFilters().stream()
                            .filter(Objects::nonNull)
                            .anyMatch(filter -> "team.id".equals(filter.getField()));
            if (hasTeamIdInFilters) {
                return true;
            }
        }

        // Check for "team.id" recursively in groups
        return queryDTO.getGroups().stream().anyMatch(group -> containsTeamIdFilterInGroup(group));
    }

    private static boolean containsTeamIdFilterInGroup(GroupFilter groupFilter) {
        if (groupFilter == null) {
            return false;
        }

        // Check for "team.id" in filters in the current group
        if (groupFilter.getFilters() != null) {
            boolean hasTeamIdInFilters =
                    groupFilter.getFilters().stream()
                            .filter(Objects::nonNull)
                            .anyMatch(filter -> "team.id".equals(filter.getField()));
            if (hasTeamIdInFilters) {
                return true;
            }
        }

        // Recursively check nested groups for "team.id"
        if (groupFilter.getGroups() != null) {
            return groupFilter.getGroups().stream()
                    .anyMatch(nestedGroup -> containsTeamIdFilterInGroup(nestedGroup));
        }

        return false;
    }

    public Optional<TeamRequestDTO> getNextEntity(Long requestId) {
        return teamRequestRepository.findNextEntity(requestId).map(teamRequestMapper::toDto);
    }

    public Optional<TeamRequestDTO> getPreviousEntity(Long requestId) {
        return teamRequestRepository.findPreviousEntity(requestId).map(teamRequestMapper::toDto);
    }

    // Fetch ticket distribution by team member
    public List<TicketDistributionDTO> getTicketDistribution(Long teamId) {
        return teamRequestRepository.findTicketDistributionByTeamId(teamId);
    }

    // Fetch unassigned tickets
    public Page<TeamRequestDTO> getUnassignedTickets(Long teamId, Pageable pageable) {
        return teamRequestRepository
                .findUnassignedTicketsByTeamId(teamId, pageable)
                .map(teamRequestMapper::toDto);
    }

    // Fetch ticket priority distribution
    public List<PriorityDistributionDTO> getPriorityDistribution(Long teamId) {
        return teamRequestRepository.findTicketPriorityDistributionByTeamId(teamId);
    }

    public TicketStatisticsDTO getTicketStatisticsByTeamId(Long teamId) {
        return teamRequestRepository.getTicketStatisticsByTeamId(teamId);
    }

    private ZonedDateTime calculateEarliestSlaDueDate(Long workflowId, Long sourceStateId) {
        // Fetch all transitions from the current state
        List<WorkflowTransition> transitions =
                workflowTransitionRepository.findTransitionsBySourceState(
                        workflowId, sourceStateId);

        if (transitions.isEmpty()) {
            throw new IllegalStateException("No transitions defined for the current state.");
        }

        // Find the transition with the lowest SLA duration
        WorkflowTransition earliestTransition =
                transitions.stream()
                        .filter(
                                t ->
                                        t.getSlaDuration()
                                                != null) // Exclude transitions without SLA duration
                        .min(
                                Comparator.comparing(
                                        WorkflowTransition
                                                ::getSlaDuration)) // Find the minimum SLA duration
                        .orElse(null);

        if (earliestTransition == null) {
            return null; // No SLA defined for any transition
        }

        // Calculate the SLA due date for the earliest transition
        return ZonedDateTime.now().plusMinutes(earliestTransition.getSlaDuration());
    }

    public Page<TeamRequestDTO> getOverdueTickets(Long teamId, Pageable pageable) {
        return teamRequestRepository
                .findOverdueTicketsByTeamId(teamId, Completed, pageable)
                .map(teamRequestMapper::toDto);
    }

    public Long countOverdueTickets(Long teamId) {
        return teamRequestRepository.countOverdueTicketsByTeamId(teamId, Completed);
    }

    public List<TicketActionCountByDateDTO> getTicketCreationTimeseries(Long teamId, int days) {
        if (days <= 0) {
            days = 7; // Default to 7 days
        }

        LocalDate startDate = LocalDate.now().minusDays(days - 1);
        List<TicketActionCountByDateDTO> trends =
                teamRequestRepository.findTicketActionByDaySeries(
                        teamId, startDate.atStartOfDay().toInstant(ZoneOffset.UTC));

        // Fill missing dates with zero counts
        Map<LocalDate, TicketActionCountByDateDTO> trendMap = new HashMap<>();
        for (TicketActionCountByDateDTO trend : trends) {
            trendMap.put(trend.getDate(), trend);
        }

        List<TicketActionCountByDateDTO> ticketByDaySeries = new ArrayList<>();
        for (int i = 0; i < days; i++) {
            LocalDate date = startDate.plusDays(i);
            TicketActionCountByDateDTO trend =
                    trendMap.getOrDefault(date, new TicketActionCountByDateDTO(date, 0L, 0L));
            ticketByDaySeries.add(trend);
        }

        return ticketByDaySeries;
    }
}
