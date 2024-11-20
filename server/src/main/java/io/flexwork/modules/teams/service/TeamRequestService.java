package io.flexwork.modules.teams.service;

import io.flexwork.modules.teams.domain.TeamRequest;
import io.flexwork.modules.teams.repository.TeamRequestRepository;
import io.flexwork.modules.teams.service.dto.TeamRequestDTO;
import io.flexwork.modules.teams.service.mapper.TeamRequestMapper;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.jclouds.rest.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TeamRequestService {

    private final TeamRequestRepository teamRequestRepository;
    private final TeamRequestMapper teamRequestMapper; // Assume a MapStruct mapper

    @Autowired
    public TeamRequestService(
            TeamRequestRepository teamRequestRepository, TeamRequestMapper teamRequestMapper) {
        this.teamRequestRepository = teamRequestRepository;
        this.teamRequestMapper = teamRequestMapper;
    }

    @Transactional(readOnly = true)
    public List<TeamRequestDTO> getAllTeamRequests() {
        return teamRequestRepository.findAll().stream()
                .map(teamRequestMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TeamRequestDTO getTeamRequestById(Long id) {
        TeamRequest teamRequest =
                teamRequestRepository
                        .findById(id)
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "TeamRequest not found with id: " + id));
        return teamRequestMapper.toDto(teamRequest);
    }

    @Transactional
    public TeamRequestDTO createTeamRequest(TeamRequestDTO teamRequestDTO) {
        TeamRequest teamRequest = teamRequestMapper.toEntity(teamRequestDTO);
        teamRequest.setCreatedDate(LocalDateTime.now()); // Example of setting default value
        teamRequest = teamRequestRepository.save(teamRequest);
        return teamRequestMapper.toDto(teamRequest);
    }

    @Transactional
    public TeamRequestDTO updateTeamRequest(Long id, TeamRequestDTO teamRequestDTO) {
        TeamRequest existingTeamRequest =
                teamRequestRepository
                        .findById(id)
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "TeamRequest not found with id: " + id));

        teamRequestMapper.updateEntity(teamRequestDTO, existingTeamRequest); // Update fields
        existingTeamRequest = teamRequestRepository.save(existingTeamRequest);
        return teamRequestMapper.toDto(existingTeamRequest);
    }

    @Transactional
    public void deleteTeamRequest(Long id) {
        if (!teamRequestRepository.existsById(id)) {
            throw new ResourceNotFoundException("TeamRequest not found with id: " + id);
        }
        teamRequestRepository.deleteById(id);
    }
}
