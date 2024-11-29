package io.flexwork.modules.teams.service;

import static io.flexwork.query.QueryUtils.createSpecification;

import io.flexwork.modules.audit.AuditLogUpdateEvent;
import io.flexwork.modules.teams.domain.TeamRequest;
import io.flexwork.modules.teams.domain.WorkflowState;
import io.flexwork.modules.teams.repository.TeamRequestRepository;
import io.flexwork.modules.teams.repository.WorkflowRepository;
import io.flexwork.modules.teams.repository.WorkflowStateRepository;
import io.flexwork.modules.teams.service.dto.PriorityDistributionDTO;
import io.flexwork.modules.teams.service.dto.TeamRequestDTO;
import io.flexwork.modules.teams.service.dto.TicketDistributionDTO;
import io.flexwork.modules.teams.service.event.NewTeamRequestCreatedEvent;
import io.flexwork.modules.teams.service.mapper.TeamRequestMapper;
import io.flexwork.query.QueryDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import java.util.List;
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
    private final WorkflowRepository workflowRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public TeamRequestService(
            TeamRequestRepository teamRequestRepository,
            TeamRequestMapper teamRequestMapper,
            WorkflowRepository workflowRepository,
            WorkflowStateRepository workflowStateRepository,
            ApplicationEventPublisher eventPublisher) {
        this.teamRequestRepository = teamRequestRepository;
        this.teamRequestMapper = teamRequestMapper;
        this.workflowRepository = workflowRepository;
        this.workflowStateRepository = workflowStateRepository;
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

        teamRequestMapper.updateEntity(teamRequestDTO, existingTeamRequest);
        TeamRequestDTO savedTeamRequest =
                teamRequestMapper.toDto(teamRequestRepository.save(existingTeamRequest));
        eventPublisher.publishEvent(
                new AuditLogUpdateEvent(this, previousTeamRequest, teamRequestDTO));
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
        return queryDTO != null
                && queryDTO.getFilters() != null
                && queryDTO.getFilters().stream()
                        .filter(Objects::nonNull)
                        .anyMatch(filter -> "team.id".equals(filter.getField()));
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
    public Page<TeamRequestDTO> getUnassignedTickets(
            Long teamId, String sortDirection, Pageable pageable) {
        if ("desc".equalsIgnoreCase(sortDirection)) {
            return teamRequestRepository
                    .findUnassignedTicketsByTeamIdDesc(teamId, pageable)
                    .map(teamRequestMapper::toDto);
        }
        return teamRequestRepository
                .findUnassignedTicketsByTeamIdAsc(teamId, pageable)
                .map(teamRequestMapper::toDto);
    }

    // Fetch ticket priority distribution
    public List<PriorityDistributionDTO> getPriorityDistribution(Long teamId) {
        return teamRequestRepository.findTicketPriorityDistributionByTeamId(teamId);
    }
}
