package io.flexwork.modules.usermanagement.web.rest;

import io.flexwork.modules.usermanagement.domain.Team;
import io.flexwork.modules.usermanagement.service.TeamService;
import io.flexwork.modules.usermanagement.service.UserService;
import io.flexwork.modules.usermanagement.service.dto.TeamDTO;
import io.flexwork.modules.usermanagement.service.dto.UserDTO;
import io.flexwork.query.QueryDTO;
import jakarta.validation.Valid;
import java.util.Optional;
import org.springframework.data.domain.Page;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/teams")
public class TeamController {

    private final UserService userService;
    private final TeamService teamService;

    public TeamController(TeamService teamService, UserService userService) {
        this.teamService = teamService;
        this.userService = userService;
    }

    // Create a new team
    @PostMapping
    public ResponseEntity<Team> createTeam(@RequestBody Team team) {
        Team createdTeam = teamService.createTeam(team);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTeam);
    }

    // Update an existing team
    @PutMapping("/{id}")
    public ResponseEntity<Team> updateTeam(@PathVariable Long id, @RequestBody Team team) {
        Team updatedTeam = teamService.updateTeam(id, team);
        return ResponseEntity.ok(updatedTeam);
    }

    // Delete a team by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeam(@PathVariable Long id) {
        teamService.deleteTeam(id);
        return ResponseEntity.noContent().build();
    }

    // Find a team by ID
    @GetMapping("/{id}")
    public ResponseEntity<Team> findTeamById(@PathVariable Long id) {
        Team team = teamService.findTeamById(id);
        return ResponseEntity.ok(team);
    }

    // Find teams
    @PostMapping("/search")
    public ResponseEntity<Page<TeamDTO>> findTeams(
            @Valid @RequestBody Optional<QueryDTO> queryDTO, Pageable pageable) {
        return new ResponseEntity<>(teamService.findTeams(queryDTO, pageable), HttpStatus.OK);
    }

    @GetMapping("/{teamId}/members")
    public ResponseEntity<Page<UserDTO>> findUsersByTeamId(
            @PathVariable Long teamId, Pageable pageable) {
        return new ResponseEntity<>(userService.getUsersByTeam(teamId, pageable), HttpStatus.OK);
    }
}
