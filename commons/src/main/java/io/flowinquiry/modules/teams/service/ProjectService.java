package io.flowinquiry.modules.teams.service;

import static io.flowinquiry.query.QueryUtils.createSpecification;

import io.flowinquiry.modules.teams.domain.Project;
import io.flowinquiry.modules.teams.domain.Team;
import io.flowinquiry.modules.teams.repository.ProjectRepository;
import io.flowinquiry.modules.teams.repository.TeamRepository;
import io.flowinquiry.modules.teams.service.dto.ProjectDTO;
import io.flowinquiry.modules.teams.service.mapper.ProjectMapper;
import io.flowinquiry.query.QueryDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProjectService {

    private final TeamRepository teamRepository;

    private final ProjectRepository projectRepository;

    private final ProjectMapper projectMapper;

    public ProjectService(
            TeamRepository teamRepository,
            ProjectRepository projectRepository,
            ProjectMapper projectMapper) {
        this.teamRepository = teamRepository;
        this.projectRepository = projectRepository;
        this.projectMapper = projectMapper;
    }

    public ProjectDTO createProject(ProjectDTO projectDTO) {
        Project project = projectMapper.toEntity(projectDTO);

        // Ensure we fetch the Team from the database before saving
        Team team =
                teamRepository
                        .findById(projectDTO.getTeamId())
                        .orElseThrow(
                                () ->
                                        new IllegalArgumentException(
                                                "Team not found with ID: "
                                                        + projectDTO.getTeamId()));
        project.setTeam(team);
        return projectMapper.toDto(projectRepository.save(project));
    }

    public Optional<ProjectDTO> getProjectById(Long id) {
        return projectRepository.findById(id).map(projectMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<ProjectDTO> findProjects(Optional<QueryDTO> queryDTO, Pageable pageable) {
        Specification<Project> spec = createSpecification(queryDTO);
        return projectRepository.findAll(spec, pageable).map(projectMapper::toDto);
    }

    public ProjectDTO updateProject(Long id, Project updatedProject) {
        return projectRepository
                .findById(id)
                .map(
                        existingProject -> {
                            existingProject.setName(updatedProject.getName());
                            existingProject.setDescription(updatedProject.getDescription());
                            existingProject.setStatus(updatedProject.getStatus());
                            existingProject.setStartDate(updatedProject.getStartDate());
                            existingProject.setEndDate(updatedProject.getEndDate());
                            existingProject.setModifiedAt(updatedProject.getModifiedAt());
                            existingProject.setModifiedBy(updatedProject.getModifiedBy());
                            return projectMapper.toDto(projectRepository.save(existingProject));
                        })
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));
    }

    public void deleteProject(Long id) {
        projectRepository.deleteById(id);
    }
}
