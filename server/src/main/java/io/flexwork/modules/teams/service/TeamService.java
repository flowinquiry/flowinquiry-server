package io.flexwork.modules.teams.service;

import static io.flexwork.query.QueryUtils.createSpecification;

import io.flexwork.modules.teams.domain.Team;
import io.flexwork.modules.teams.domain.TeamRole;
import io.flexwork.modules.teams.repository.TeamRepository;
import io.flexwork.modules.teams.repository.TeamRoleRepository;
import io.flexwork.modules.teams.service.dto.TeamDTO;
import io.flexwork.modules.teams.service.event.NewUsersAddedIntoTeamEvent;
import io.flexwork.modules.teams.service.event.RemoveUserOutOfTeamEvent;
import io.flexwork.modules.teams.service.mapper.TeamMapper;
import io.flexwork.modules.usermanagement.domain.User;
import io.flexwork.modules.usermanagement.domain.UserTeam;
import io.flexwork.modules.usermanagement.domain.UserTeamId;
import io.flexwork.modules.usermanagement.repository.UserRepository;
import io.flexwork.modules.usermanagement.repository.UserTeamRepository;
import io.flexwork.modules.usermanagement.service.dto.UserDTO;
import io.flexwork.modules.usermanagement.service.dto.UserWithTeamRoleDTO;
import io.flexwork.modules.usermanagement.service.mapper.UserMapper;
import io.flexwork.query.QueryDTO;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TeamService {

    private final TeamRepository teamRepository;

    private final UserRepository userRepository;

    private final UserTeamRepository userTeamRepository;

    private final TeamRoleRepository teamRoleRepository;

    private final TeamMapper teamMapper;

    private final UserMapper userMapper;

    private final ApplicationEventPublisher eventPublisher;

    public TeamService(
            TeamRepository teamRepository,
            UserRepository userRepository,
            UserTeamRepository userTeamRepository,
            TeamRoleRepository teamRoleRepository,
            TeamMapper teamMapper,
            UserMapper userMapper,
            ApplicationEventPublisher eventPublisher) {
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
        this.userTeamRepository = userTeamRepository;
        this.teamRoleRepository = teamRoleRepository;
        this.teamMapper = teamMapper;
        this.userMapper = userMapper;
        this.eventPublisher = eventPublisher;
    }

    public TeamDTO createTeam(TeamDTO teamDTO) {
        Team team = teamMapper.toEntity(teamDTO);
        return teamMapper.toDto(teamRepository.save(team));
    }

    public TeamDTO updateTeam(TeamDTO updatedTeam) {
        Team existingTeam =
                teamRepository
                        .findById(updatedTeam.getId())
                        .orElseThrow(
                                () ->
                                        new EntityNotFoundException(
                                                "Team not found with id: " + updatedTeam.getId()));
        teamMapper.updateFromDto(updatedTeam, existingTeam);

        return teamMapper.toDto(teamRepository.save(existingTeam));
    }

    public void deleteTeam(Long id) {
        Team existingTeam =
                teamRepository
                        .findById(id)
                        .orElseThrow(
                                () -> new EntityNotFoundException("Team not found with id: " + id));

        teamRepository.delete(existingTeam);
    }

    public void deleteTeams(List<Long> ids) {
        teamRepository.deleteAllByIdInBatch(ids);
    }

    @Transactional(readOnly = true)
    public Optional<TeamDTO> findTeamById(Long id) {
        return teamRepository.findById(id).map(teamMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<TeamDTO> findTeams(Optional<QueryDTO> queryDTO, Pageable pageable) {
        Specification<Team> spec = createSpecification(queryDTO);
        return teamRepository.findAllDTOs(spec, pageable);
    }

    @Transactional(readOnly = true)
    public List<TeamDTO> findAllTeamsByUserId(Long userId) {
        return teamRepository.findAllTeamsByUserId(userId).stream().map(teamMapper::toDto).toList();
    }

    public List<UserWithTeamRoleDTO> getUsersByTeam(Long teamId) {
        return teamRepository.findUsersByTeamId(teamId);
    }

    @Transactional(readOnly = true)
    public List<UserDTO> findUsersNotInTeam(String searchTerm, Long teamId, Pageable pageable) {
        return userMapper.toDtos(teamRepository.findUsersNotInTeam(searchTerm, teamId, pageable));
    }

    public void addUsersToTeam(List<Long> userIds, String roleName, Long teamId) {
        // Fetch the authority entity
        Team team =
                teamRepository
                        .findById(teamId)
                        .orElseThrow(
                                () -> new IllegalArgumentException("Team not found: " + teamId));

        TeamRole teamRole =
                teamRoleRepository
                        .findById(roleName)
                        .orElseThrow(
                                () ->
                                        new IllegalArgumentException(
                                                "Invalid role name: " + roleName));

        // Fetch the users and associate them with the authority
        List<User> users = userRepository.findAllById(userIds);

        // Ensure all user IDs are valid
        if (users.size() != userIds.size()) {
            throw new IllegalArgumentException("Some user IDs are invalid.");
        }

        List<UserTeam> userTeams =
                users.stream()
                        .map(
                                user ->
                                        new UserTeam(
                                                new UserTeamId(
                                                        user.getId(), team.getId(), roleName),
                                                user,
                                                team,
                                                teamRole))
                        .toList();

        // Save all UserTeam entities in a batch
        userTeamRepository.saveAll(userTeams);

        eventPublisher.publishEvent(
                new NewUsersAddedIntoTeamEvent(this, userIds, teamId, roleName));
    }

    @Transactional
    public void removeUserFromTeam(Long userId, Long teamId) {
        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(
                                () -> new IllegalArgumentException("User not found: " + userId));

        Team team =
                teamRepository
                        .findById(teamId)
                        .orElseThrow(
                                () -> new IllegalArgumentException("Team not found: " + teamId));

        // Remove the team from the user's teams set
        if (user.getTeams().contains(team)) {
            user.getTeams().remove(team);
            userRepository.save(user);
        }

        eventPublisher.publishEvent(new RemoveUserOutOfTeamEvent(this, teamId, userId));
    }

    public String getUserRoleInTeam(Long userId, Long teamId) {
        String role = teamRepository.findUserRoleInTeam(userId, teamId);
        return (StringUtils.isNotEmpty(role)) ? role : "Guest";
    }
}
