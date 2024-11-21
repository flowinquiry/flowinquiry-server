package io.flexwork.modules.teams.web.rest;

import io.flexwork.modules.teams.service.TeamRequestService;
import io.flexwork.modules.teams.service.dto.TeamRequestDTO;
import io.flexwork.query.QueryDTO;
import jakarta.validation.Valid;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/team-requests")
public class TeamRequestController {

    private final TeamRequestService teamRequestService;

    public TeamRequestController(TeamRequestService teamRequestService) {
        this.teamRequestService = teamRequestService;
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<Page<TeamRequestDTO>> getAllTeamRequests(Pageable pageable) {
        return ResponseEntity.ok(teamRequestService.getAllTeamRequests(pageable));
    }

    @PostMapping("/search")
    public ResponseEntity<Page<TeamRequestDTO>> findTeamRequests(
            @Valid @RequestBody Optional<QueryDTO> queryDTO, Pageable pageable) {
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
        return ResponseEntity.ok(teamRequestService.updateTeamRequest(id, teamRequestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeamRequest(@PathVariable Long id) {
        teamRequestService.deleteTeamRequest(id);
        return ResponseEntity.noContent().build();
    }
}
