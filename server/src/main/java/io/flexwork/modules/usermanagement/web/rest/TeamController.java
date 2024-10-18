package io.flexwork.modules.usermanagement.web.rest;

import static io.flexwork.query.QueryUtils.parseFiltersFromParams;

import io.flexwork.modules.usermanagement.domain.Team;
import io.flexwork.modules.usermanagement.service.TeamService;
import io.flexwork.modules.usermanagement.service.UserService;
import io.flexwork.modules.usermanagement.service.dto.TeamDTO;
import io.flexwork.modules.usermanagement.service.dto.UserDTO;
import io.flexwork.modules.usermanagement.service.mapper.TeamMapper;
import io.flexwork.query.QueryFilter;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/teams")
public class TeamController {

    private final TeamMapper teamMapper;

    private final UserService userService;
    private final TeamService teamService;

    public TeamController(TeamMapper teamMapper, TeamService teamService, UserService userService) {
        this.teamMapper = teamMapper;
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
    @GetMapping
    public ResponseEntity<Page<TeamDTO>> findTeams(
            @RequestParam Map<String, String> params, Pageable pageable) {
        List<QueryFilter> filters = parseFiltersFromParams(params);
        Page<Team> teams = teamService.findTeams(filters, pageable);
        return new ResponseEntity<>(teams.map(teamMapper::teamToTeamDTO), HttpStatus.OK);
    }

    @GetMapping("/{teamId}/members")
    public ResponseEntity<Page<UserDTO>> findUsersByTeamId(
            @PathVariable Long teamId, Pageable pageable) {
        return new ResponseEntity<>(userService.getUsersByTeam(teamId, pageable), HttpStatus.OK);
    }
}
