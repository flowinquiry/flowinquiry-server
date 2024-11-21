package io.flexwork.modules.teams.service;

import static io.flexwork.query.QueryUtils.createSpecification;

import io.flexwork.modules.teams.domain.TeamRequest;
import io.flexwork.modules.teams.repository.TeamRequestRepository;
import io.flexwork.modules.teams.service.dto.TeamRequestDTO;
import io.flexwork.modules.teams.service.mapper.TeamRequestMapper;
import io.flexwork.query.QueryDTO;
import java.time.LocalDateTime;
import java.util.Optional;
import org.jclouds.rest.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TeamRequestService {

    private final TeamRequestRepository teamRequestRepository;
    private final TeamRequestMapper teamRequestMapper;

    @Autowired
    public TeamRequestService(
            TeamRequestRepository teamRequestRepository, TeamRequestMapper teamRequestMapper) {
        this.teamRequestRepository = teamRequestRepository;
        this.teamRequestMapper = teamRequestMapper;
    }

    @Transactional(readOnly = true)
    public Page<TeamRequestDTO> getAllTeamRequests(Pageable pageable) {
        return teamRequestRepository.findAll(pageable).map(teamRequestMapper::toDto);
    }

    public Page<TeamRequestDTO> findTeamRequests(Optional<QueryDTO> queryDTO, Pageable pageable) {
        Specification<TeamRequest> spec = createSpecification(queryDTO);
        return teamRequestRepository
                .findAllWithEagerRelationships(spec, pageable)
                .map(teamRequestMapper::toDto);
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
        teamRequest.setCreatedDate(LocalDateTime.now());
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

        teamRequestMapper.updateEntity(teamRequestDTO, existingTeamRequest);
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
