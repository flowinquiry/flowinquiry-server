package io.flexwork.modules.usermanagement.service;

import static io.flexwork.query.QueryUtils.createSpecification;

import io.flexwork.modules.usermanagement.domain.Team;
import io.flexwork.modules.usermanagement.domain.User;
import io.flexwork.modules.usermanagement.repository.TeamRepository;
import io.flexwork.modules.usermanagement.repository.UserRepository;
import io.flexwork.modules.usermanagement.service.dto.TeamDTO;
import io.flexwork.modules.usermanagement.service.dto.UserDTO;
import io.flexwork.modules.usermanagement.service.mapper.TeamMapper;
import io.flexwork.modules.usermanagement.service.mapper.UserMapper;
import io.flexwork.query.QueryDTO;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TeamService {

    private final TeamRepository teamRepository;

    private final UserRepository userRepository;

    private final TeamMapper teamMapper;

    private final UserMapper userMapper;

    public TeamService(
            TeamRepository teamRepository,
            UserRepository userRepository,
            TeamMapper teamMapper,
            UserMapper userMapper) {
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
        this.teamMapper = teamMapper;
        this.userMapper = userMapper;
    }

    public TeamDTO createTeam(TeamDTO teamDTO) {
        Team team = teamMapper.toEntity(teamDTO);
        return teamMapper.toDto(teamRepository.save(team));
    }

    public Team updateTeam(TeamDTO updatedTeam) {
        Team existingTeam =
                teamRepository
                        .findById(updatedTeam.getId())
                        .orElseThrow(
                                () ->
                                        new EntityNotFoundException(
                                                "Team not found with id: " + updatedTeam.getId()));
        teamMapper.updateFromDto(updatedTeam, existingTeam);

        return teamRepository.save(existingTeam);
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

    public Team findTeamById(Long id) {
        return teamRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Team not found with id: " + id));
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

    @Transactional(readOnly = true)
    public List<UserDTO> findUsersNotInTeam(String searchTerm, Long teamId, Pageable pageable) {
        return userMapper.toDtos(teamRepository.findUsersNotInTeam(searchTerm, teamId, pageable));
    }

    @Transactional
    public void addUsersToTeam(List<Long> userIds, Long teamId) {
        // Fetch the authority entity
        Team team =
                teamRepository
                        .findById(teamId)
                        .orElseThrow(
                                () -> new IllegalArgumentException("Team not found: " + teamId));

        // Fetch the users and associate them with the authority
        List<User> users = userRepository.findAllById(userIds);
        for (User user : users) {
            user.getTeams().add(team);
        }

        // Save all updated users
        userRepository.saveAll(users);
    }

    @Transactional
    public void removeUserFromTeam(Long userId, Long teamId) {
        // Find the user
        User user =
                userRepository
                        .findByIdWithTeams(userId)
                        .orElseThrow(
                                () -> new IllegalArgumentException("User not found: " + userId));

        // Find the team
        Team team =
                teamRepository
                        .findById(teamId)
                        .orElseThrow(
                                () -> new IllegalArgumentException("Team not found: " + teamId));

        // Remove the team from the user's teams set
        if (user.getTeams().contains(team)) {
            user.getTeams().remove(team);
            userRepository.save(user); // Save the updated user
        }
    }
}
