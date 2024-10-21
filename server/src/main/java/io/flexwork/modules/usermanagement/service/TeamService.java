package io.flexwork.modules.usermanagement.service;

import static io.flexwork.query.QueryUtils.createSpecification;

import io.flexwork.modules.usermanagement.domain.Team;
import io.flexwork.modules.usermanagement.repository.TeamRepository;
import io.flexwork.query.QueryDTO;
import jakarta.persistence.EntityNotFoundException;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class TeamService {

    private TeamRepository teamRepository;

    public TeamService(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    public Team createTeam(Team team) {
        return teamRepository.save(team);
    }

    public Team updateTeam(Long id, Team updatedTeam) {
        // Find existing team
        Team existingTeam =
                teamRepository
                        .findById(id)
                        .orElseThrow(
                                () -> new EntityNotFoundException("Team not found with id: " + id));

        // Update team details
        existingTeam.setName(updatedTeam.getName());
        existingTeam.setSlogan(updatedTeam.getSlogan());
        existingTeam.setDescription(updatedTeam.getDescription());
        existingTeam.setLogoUrl(updatedTeam.getLogoUrl());

        // Save updated team
        return teamRepository.save(existingTeam);
    }

    public void deleteTeam(Long id) {
        // Check if the team exists
        Team existingTeam =
                teamRepository
                        .findById(id)
                        .orElseThrow(
                                () -> new EntityNotFoundException("Team not found with id: " + id));

        // Delete the team
        teamRepository.delete(existingTeam);
    }

    public Team findTeamById(Long id) {
        return teamRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Team not found with id: " + id));
    }

    public Page<Team> findTeams(Optional<QueryDTO> queryDTO, Pageable pageable) {
        Specification<Team> spec = createSpecification(queryDTO);
        return teamRepository.findAll(spec, pageable);
    }
}
