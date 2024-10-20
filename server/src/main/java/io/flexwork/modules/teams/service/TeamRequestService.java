package io.flexwork.modules.teams.service;

import io.flexwork.modules.teams.domain.TeamRequest;
import io.flexwork.modules.teams.repository.TeamRequestRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class TeamRequestService {

    private final TeamRequestRepository teamRequestRepository;

    public TeamRequestService(TeamRequestRepository teamRequestRepository) {
        this.teamRequestRepository = teamRequestRepository;
    }

    public List<TeamRequest> getAllTeamRequests() {
        return teamRequestRepository.findAll();
    }

    public TeamRequest getTeamRequestById(Long id) {
        return teamRequestRepository
                .findById(id)
                .orElseThrow(() -> new RuntimeException("Team request not found"));
    }

    public TeamRequest createTeamRequest(TeamRequest teamRequest) {
        return teamRequestRepository.save(teamRequest);
    }

    public TeamRequest updateTeamRequest(Long id, TeamRequest updatedRequest) {
        TeamRequest request = getTeamRequestById(id);
        request.setCurrentStatus(updatedRequest.getCurrentStatus());
        return teamRequestRepository.save(request);
    }

    public void deleteTeamRequest(Long id) {
        teamRequestRepository.deleteById(id);
    }
}
