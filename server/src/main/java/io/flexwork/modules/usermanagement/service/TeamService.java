package io.flexwork.modules.usermanagement.service;

import static io.flexwork.query.QueryUtils.createSpecification;

import io.flexwork.modules.usermanagement.domain.Team;
import io.flexwork.modules.usermanagement.repository.TeamRepository;
import io.flexwork.modules.usermanagement.service.dto.TeamDTO;
import io.flexwork.modules.usermanagement.service.mapper.TeamMapper;
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

    private TeamRepository teamRepository;

    private TeamMapper teamMapper;

    public TeamService(TeamRepository teamRepository, TeamMapper teamMapper) {
        this.teamMapper = teamMapper;
        this.teamRepository = teamRepository;
    }

    public TeamDTO createTeam(TeamDTO teamDTO) {
        Team team = teamMapper.teamDTOToTeam(teamDTO);
        return teamMapper.teamToTeamDTO(teamRepository.save(team));
    }

    public Team updateTeam(TeamDTO updatedTeam) {
        Team existingTeam =
                teamRepository
                        .findById(updatedTeam.getId())
                        .orElseThrow(
                                () ->
                                        new EntityNotFoundException(
                                                "Team not found with id: " + updatedTeam.getId()));
        teamMapper.updateTeamFromDTO(updatedTeam, existingTeam);

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
        return teamRepository.findAllTeamsByUserId(userId).stream()
                .map(teamMapper::teamToTeamDTO)
                .toList();
    }
}
