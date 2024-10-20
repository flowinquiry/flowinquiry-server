package io.flexwork.modules.teams.web.rest;

import io.flexwork.modules.teams.domain.TeamRequestStatus;
import io.flexwork.modules.teams.service.TeamRequestStatusService;
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
@RequestMapping("/api/teams/request-statuses")
public class TeamRequestStatusController {

    private final TeamRequestStatusService teamRequestStatusService;

    public TeamRequestStatusController(TeamRequestStatusService teamRequestStatusService) {
        this.teamRequestStatusService = teamRequestStatusService;
    }

    @GetMapping
    public List<TeamRequestStatus> getAllTeamRequestStatuses() {
        return teamRequestStatusService.getAllTeamRequestStatuses();
    }

    @GetMapping("/{id}")
    public TeamRequestStatus getTeamRequestStatusById(@PathVariable Long id) {
        return teamRequestStatusService.getTeamRequestStatusById(id);
    }

    @PostMapping
    public TeamRequestStatus createTeamRequestStatus(
            @RequestBody TeamRequestStatus teamRequestStatus) {
        return teamRequestStatusService.createTeamRequestStatus(teamRequestStatus);
    }

    @PutMapping("/{id}")
    public TeamRequestStatus updateTeamRequestStatus(
            @PathVariable Long id, @RequestBody TeamRequestStatus updatedStatus) {
        return teamRequestStatusService.updateTeamRequestStatus(id, updatedStatus);
    }

    @DeleteMapping("/{id}")
    public void deleteTeamRequestStatus(@PathVariable Long id) {
        teamRequestStatusService.deleteTeamRequestStatus(id);
    }
}
