package io.flowinquiry.modules.teams.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.flowinquiry.IntegrationTest;
import io.flowinquiry.exceptions.ResourceNotFoundException;
import io.flowinquiry.modules.teams.domain.ProjectStatus;
import io.flowinquiry.modules.teams.service.dto.ProjectDTO;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@Transactional
public class ProjectServiceIT {
    private @Autowired ProjectService projectService;

    @Test
    public void shouldCreateProjectSuccessfully() {
        ProjectDTO projectDTO =
                ProjectDTO.builder()
                        .name("Sample project")
                        .description("Project description")
                        .status(ProjectStatus.Active)
                        .teamId(1L)
                        .build();
        ProjectDTO savedProject = projectService.createProject(projectDTO);
        savedProject =
                projectService
                        .getProjectById(savedProject.getId())
                        .orElseThrow(
                                () -> new ResourceNotFoundException("Issue of saving project"));
        assertThat(savedProject)
                .extracting(ProjectDTO::getName, ProjectDTO::getDescription, ProjectDTO::getTeamId)
                .containsExactly("Sample project", "Project description", 1L);
    }

    @Test
    public void shouldCreateProjectFailedBecauseTeamIsNotExisted() {
        ProjectDTO projectDTO =
                ProjectDTO.builder()
                        .name("Sample project")
                        .description("Project description")
                        .status(ProjectStatus.Active)
                        .teamId(100L)
                        .build();
        assertThatThrownBy(() -> projectService.createProject(projectDTO))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    public void shouldDeleteProjectSuccessfully() {
        ProjectDTO projectDTO =
                ProjectDTO.builder()
                        .name("Sample project")
                        .description("Project description")
                        .status(ProjectStatus.Active)
                        .teamId(1L)
                        .build();
        ProjectDTO savedProject = projectService.createProject(projectDTO);
        projectService.deleteProject(savedProject.getId());

        Optional<ProjectDTO> notFoundProject = projectService.getProjectById(savedProject.getId());
        assertThat(notFoundProject.isEmpty()).isTrue();
    }

    @Test
    public void shouldUpdateProjectSuccessfully() {
        ProjectDTO projectDTO =
                ProjectDTO.builder()
                        .name("Sample project")
                        .description("Project description")
                        .status(ProjectStatus.Active)
                        .teamId(1L)
                        .build();
        ProjectDTO savedProject = projectService.createProject(projectDTO);
        savedProject.setName("New Project");
        savedProject.setStatus(ProjectStatus.Closed);
        projectService.updateProject(savedProject.getId(), savedProject);

        savedProject = projectService.getProjectById(savedProject.getId()).orElseThrow();
        assertThat(savedProject)
                .extracting(ProjectDTO::getName, ProjectDTO::getStatus)
                .containsExactly("New Project", ProjectStatus.Closed);
    }
}
