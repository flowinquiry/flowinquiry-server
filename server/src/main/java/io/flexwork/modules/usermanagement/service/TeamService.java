package io.flexwork.modules.usermanagement.service;

import io.flexwork.modules.usermanagement.domain.Team;
import io.flexwork.modules.usermanagement.repository.TeamRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
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
        existingTeam.setTeamLogoUrl(updatedTeam.getTeamLogoUrl());

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

    public List<Team> findAllTeams() {
        return teamRepository.findAll();
    }
}
