package io.flexwork.modules.teams.service;

import io.flexwork.modules.teams.domain.TeamRequestStatus;
import io.flexwork.modules.teams.repository.TeamRequestStatusRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class TeamRequestStatusService {

    private final TeamRequestStatusRepository teamRequestStatusRepository;

    public TeamRequestStatusService(TeamRequestStatusRepository teamRequestStatusRepository) {
        this.teamRequestStatusRepository = teamRequestStatusRepository;
    }

    public List<TeamRequestStatus> getAllTeamRequestStatuses() {
        return teamRequestStatusRepository.findAll();
    }

    public TeamRequestStatus getTeamRequestStatusById(Long id) {
        return teamRequestStatusRepository
                .findById(id)
                .orElseThrow(() -> new RuntimeException("Team request status not found"));
    }

    public TeamRequestStatus createTeamRequestStatus(TeamRequestStatus teamRequestStatus) {
        return teamRequestStatusRepository.save(teamRequestStatus);
    }

    public TeamRequestStatus updateTeamRequestStatus(Long id, TeamRequestStatus updatedStatus) {
        TeamRequestStatus status = getTeamRequestStatusById(id);
        status.setComments(updatedStatus.getComments());
        return teamRequestStatusRepository.save(status);
    }

    public void deleteTeamRequestStatus(Long id) {
        teamRequestStatusRepository.deleteById(id);
    }
}
