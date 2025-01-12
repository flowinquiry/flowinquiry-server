package io.flowinquiry.modules.teams.controller;

import io.flowinquiry.modules.fss.ResourceRemoveEvent;
import io.flowinquiry.modules.fss.service.StorageService;
import io.flowinquiry.modules.teams.service.TeamService;
import io.flowinquiry.modules.teams.service.dto.TeamDTO;
import io.flowinquiry.modules.usermanagement.service.dto.UserDTO;
import io.flowinquiry.modules.usermanagement.service.dto.UserWithTeamRoleDTO;
import io.flowinquiry.query.QueryDTO;
import jakarta.json.Json;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/teams")
public class TeamController {

    private final TeamService teamService;
    private final StorageService storageService;
    private final ApplicationEventPublisher eventPublisher;

    public TeamController(
            TeamService teamService,
            StorageService storageService,
            ApplicationEventPublisher eventPublisher) {
        this.teamService = teamService;
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

    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<TeamDTO> updateTeam(
            @RequestPart("teamDTO") TeamDTO team,
            @RequestPart(value = "file", required = false) MultipartFile file)
            throws Exception {
        Optional<String> fileRemovedPath = Optional.empty();

        if (file != null && !file.isEmpty()) {
            fileRemovedPath = Optional.ofNullable(team.getLogoUrl());
            String teamLogoPath =
                    storageService.uploadImage(
                            "teams", UUID.randomUUID().toString(), file.getInputStream());
            team.setLogoUrl(teamLogoPath);
        }
        TeamDTO updatedTeam = teamService.updateTeam(team);
        // Remove the old logo
        fileRemovedPath.ifPresent(
                s -> eventPublisher.publishEvent(new ResourceRemoveEvent(this, s)));
        return ResponseEntity.ok(updatedTeam);
    }

    // Delete a team by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeam(@PathVariable("id") Long id) {
        teamService.deleteTeam(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteTeams(@RequestBody List<Long> ids) {
        teamService.deleteTeams(ids);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TeamDTO> findTeamById(@PathVariable("id") Long id) {
        return teamService
                .findTeamById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/search")
    public ResponseEntity<Page<TeamDTO>> findTeams(
            @Valid @RequestBody Optional<QueryDTO> queryDTO, Pageable pageable) {
        return new ResponseEntity<>(teamService.findTeams(queryDTO, pageable), HttpStatus.OK);
    }

    @GetMapping("/{teamId}/members")
    public ResponseEntity<List<UserWithTeamRoleDTO>> findUsersByTeamId(
            @PathVariable("teamId") Long teamId) {
        return new ResponseEntity<>(teamService.getUsersByTeam(teamId), HttpStatus.OK);
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<List<TeamDTO>> getTeamsByUserId(@PathVariable("userId") Long userId) {
        List<TeamDTO> teams = teamService.findAllTeamsByUserId(userId);
        return (teams.isEmpty()) ? ResponseEntity.noContent().build() : ResponseEntity.ok(teams);
    }

    @PostMapping("/{teamId}/add-users")
    public ResponseEntity<Void> addUsersToTeam(
            @PathVariable("teamId") Long teamId,
            @RequestBody ListUserIdsAndRoleDTO userIdsAndRoleDTO) {
        teamService.addUsersToTeam(
                userIdsAndRoleDTO.getUserIds(), userIdsAndRoleDTO.getRole(), teamId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/searchUsersNotInTeam")
    public ResponseEntity<List<UserDTO>> findUsersNotInTeam(
            @RequestParam("userTerm") String searchTerm, @RequestParam("teamId") Long teamId) {
        PageRequest pageRequest = PageRequest.of(0, 20);
        List<UserDTO> users = teamService.findUsersNotInTeam(searchTerm, teamId, pageRequest);
        return ResponseEntity.ok(users);
    }

    @DeleteMapping("/{teamId}/users/{userId}")
    public ResponseEntity<Void> removeUserFromTeam(
            @PathVariable("userId") Long userId, @PathVariable("teamId") Long teamId) {
        teamService.removeUserFromTeam(userId, teamId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{teamId}/users/{userId}/role")
    public ResponseEntity<String> getUserRoleInTeam(
            @PathVariable("teamId") Long teamId, @PathVariable("userId") Long userId) {
        String role = teamService.getUserRoleInTeam(userId, teamId);
        return ResponseEntity.ok(Json.createObjectBuilder().add("role", role).build().toString());
    }

    @Getter
    @Setter
    public static class ListUserIdsAndRoleDTO {
        private List<Long> userIds;
        private String role;
    }
}
