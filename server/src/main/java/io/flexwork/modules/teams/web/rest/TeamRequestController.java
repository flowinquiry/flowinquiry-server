package io.flexwork.modules.teams.web.rest;

import io.flexwork.modules.teams.service.TeamRequestService;
import io.flexwork.modules.teams.service.dto.TeamRequestDTO;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/team-requests")
public class TeamRequestController {

    private final TeamRequestService teamRequestService;

    public TeamRequestController(TeamRequestService teamRequestService) {
        this.teamRequestService = teamRequestService;
    }

    @GetMapping
    public ResponseEntity<List<TeamRequestDTO>> getAllTeamRequests() {
        return ResponseEntity.ok(teamRequestService.getAllTeamRequests());
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
