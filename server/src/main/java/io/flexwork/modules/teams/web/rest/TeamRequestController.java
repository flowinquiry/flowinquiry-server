package io.flexwork.modules.teams.web.rest;

import io.flexwork.modules.teams.service.TeamRequestService;
import io.flexwork.modules.teams.service.WorkflowTransitionHistoryService;
import io.flexwork.modules.teams.service.dto.PriorityDistributionDTO;
import io.flexwork.modules.teams.service.dto.TeamRequestDTO;
import io.flexwork.modules.teams.service.dto.TicketDistributionDTO;
import io.flexwork.modules.teams.service.dto.TransitionItemCollectionDTO;
import io.flexwork.query.QueryDTO;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/team-requests")
public class TeamRequestController {

    private final TeamRequestService teamRequestService;
    private final WorkflowTransitionHistoryService workflowTransitionHistoryService;

    public TeamRequestController(
            TeamRequestService teamRequestService,
            WorkflowTransitionHistoryService workflowTransitionHistoryService) {
        this.teamRequestService = teamRequestService;
        this.workflowTransitionHistoryService = workflowTransitionHistoryService;
    }

    @PostMapping("/search")
    public ResponseEntity<Page<TeamRequestDTO>> findTeamRequests(
            @Valid @RequestBody QueryDTO queryDTO, Pageable pageable) {
        Page<TeamRequestDTO> teamRequests = teamRequestService.findTeamRequests(queryDTO, pageable);
        return new ResponseEntity<>(teamRequests, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TeamRequestDTO> getTeamRequestById(@PathVariable Long id) {
        return ResponseEntity.ok(teamRequestService.getTeamRequestById(id));
    }

    @PostMapping
    public ResponseEntity<TeamRequestDTO> createTeamRequest(
            @RequestBody TeamRequestDTO teamRequestDTO) {
        TeamRequestDTO createdTeamRequest = teamRequestService.createTeamRequest(teamRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTeamRequest);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TeamRequestDTO> updateTeamRequest(
            @PathVariable Long id, @RequestBody TeamRequestDTO teamRequestDTO) {
        if (!id.equals(teamRequestDTO.getId())) {
            throw new IllegalArgumentException("Id in URL and payload do not match");
        }
        return ResponseEntity.ok(teamRequestService.updateTeamRequest(teamRequestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeamRequest(@PathVariable Long id) {
        teamRequestService.deleteTeamRequest(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{currentId}/next")
    public ResponseEntity<TeamRequestDTO> getNextEntity(@PathVariable Long currentId) {
        return teamRequestService
                .getNextEntity(currentId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{currentId}/previous")
    public ResponseEntity<TeamRequestDTO> getPreviousEntity(@PathVariable Long currentId) {
        return teamRequestService
                .getPreviousEntity(currentId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Endpoint to get ticket distribution for a specific team
    @GetMapping("/{teamId}/ticket-distribution")
    public List<TicketDistributionDTO> getTicketDistribution(@PathVariable Long teamId) {
        return teamRequestService.getTicketDistribution(teamId);
    }

    // Endpoint to get unassigned tickets for a specific team
    @GetMapping("/{teamId}/unassigned-tickets")
    public Page<TeamRequestDTO> getUnassignedTickets(
            @PathVariable Long teamId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "priority") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {

        Pageable pageable = PageRequest.of(page, size);
        return teamRequestService.getUnassignedTickets(teamId, sortDirection, pageable);
    }

    // Endpoint to get priority distribution for a specific team
    @GetMapping("/{teamId}/priority-distribution")
    public List<PriorityDistributionDTO> getPriorityDistribution(@PathVariable Long teamId) {
        return teamRequestService.getPriorityDistribution(teamId);
    }

    /**
     * Fetch the workflow transition history for a specific ticket/request.
     *
     * @param teamRequestId the ID of the ticket
     * @return a TicketHistoryDto containing workflow details and transitions
     */
    @GetMapping("/{ticketId}/states-history")
    public ResponseEntity<TransitionItemCollectionDTO> getTicketHistory(
            @PathVariable Long teamRequestId) {
        TransitionItemCollectionDTO ticketHistory =
                workflowTransitionHistoryService.getTransitionHistoryByTicketId(teamRequestId);

        return ResponseEntity.ok(ticketHistory);
    }
}
