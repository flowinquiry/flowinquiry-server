package io.flexwork.modules.teams.web.rest;

import io.flexwork.modules.teams.domain.TeamRequest;
import io.flexwork.modules.teams.service.TeamRequestService;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/teams/requests")
public class TeamRequestController {

    private final TeamRequestService teamRequestService;

    public TeamRequestController(TeamRequestService teamRequestService) {
        this.teamRequestService = teamRequestService;
    }

    @GetMapping
    public List<TeamRequest> getAllTeamRequests() {
        return teamRequestService.getAllTeamRequests();
    }

    @GetMapping("/{id}")
    public TeamRequest getTeamRequestById(@PathVariable Long id) {
        return teamRequestService.getTeamRequestById(id);
    }

    @PostMapping
    public TeamRequest createTeamRequest(@RequestBody TeamRequest teamRequest) {
        return teamRequestService.createTeamRequest(teamRequest);
    }

    @PutMapping("/{id}")
    public TeamRequest updateTeamRequest(
            @PathVariable Long id, @RequestBody TeamRequest updatedRequest) {
        return teamRequestService.updateTeamRequest(id, updatedRequest);
    }

    @DeleteMapping("/{id}")
    public void deleteTeamRequest(@PathVariable Long id) {
        teamRequestService.deleteTeamRequest(id);
    }
}
