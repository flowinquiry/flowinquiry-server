package io.flexwork.modules.usermanagement.web.rest;

import io.flexwork.modules.fss.ResourceRemoveEvent;
import io.flexwork.modules.fss.service.StorageService;
import io.flexwork.modules.usermanagement.domain.Team;
import io.flexwork.modules.usermanagement.service.TeamService;
import io.flexwork.modules.usermanagement.service.UserService;
import io.flexwork.modules.usermanagement.service.dto.TeamDTO;
import io.flexwork.modules.usermanagement.service.dto.UserDTO;
import io.flexwork.query.QueryDTO;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/teams")
public class TeamController {

    private final UserService userService;
    private final TeamService teamService;
    private final StorageService storageService;
    private final ApplicationEventPublisher eventPublisher;

    public TeamController(
            TeamService teamService,
            UserService userService,
            StorageService storageService,
            ApplicationEventPublisher eventPublisher) {
        this.teamService = teamService;
        this.userService = userService;
        this.storageService = storageService;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Create new team
     *
     * @param team
     * @return
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<TeamDTO> createTeam(
            @RequestPart("teamDTO") TeamDTO team,
            @RequestPart(value = "file", required = false) MultipartFile file)
            throws Exception {
        if (file != null && !file.isEmpty()) {
            String avatarPath =
                    storageService.uploadImage(
                            "teams", UUID.randomUUID().toString(), file.getInputStream());
            team.setLogoUrl(avatarPath);
        }
        TeamDTO createdTeam = teamService.createTeam(team);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdTeam);
    }

    // Update an existing team
    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Team> updateTeam(
            @RequestPart("teamDTO") TeamDTO team,
            @RequestPart(value = "file", required = false) MultipartFile file)
            throws Exception {
        Optional<String> fileRemovedPath = Optional.empty();

        if (file != null && !file.isEmpty()) {
            fileRemovedPath = Optional.of(team.getLogoUrl());
            String teamLogoPath =
                    storageService.uploadImage(
                            "teams", UUID.randomUUID().toString(), file.getInputStream());
            team.setLogoUrl(teamLogoPath);
        }
        Team updatedTeam = teamService.updateTeam(team);
        // Remove the old logo
        if (fileRemovedPath.isPresent()) {
            eventPublisher.publishEvent(new ResourceRemoveEvent(this, fileRemovedPath.get()));
        }
        return ResponseEntity.ok(updatedTeam);
    }

    // Delete a team by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeam(@PathVariable Long id) {
        teamService.deleteTeam(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteTeams(@RequestBody List<Long> ids) {
        teamService.deleteTeams(ids);
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
