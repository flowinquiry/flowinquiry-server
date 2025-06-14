package io.flowinquiry.modules.teams.service.mapper;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.flowinquiry.modules.teams.domain.Project;
import io.flowinquiry.modules.teams.domain.ProjectStatus;
import io.flowinquiry.modules.teams.domain.Team;
import io.flowinquiry.modules.teams.service.dto.ProjectDTO;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

public class ProjectMapperTest {

    private ProjectMapper projectMapper = Mappers.getMapper(ProjectMapper.class);

    @Test
    public void testToDto() {
        // Given
        Instant now = Instant.now();
        Team team = Team.builder().id(1L).name("Team Name").build();

        Project project =
                Project.builder()
                        .id(1L)
                        .name("Project Name")
                        .description("Project Description")
                        .shortName("PRJ")
                        .team(team)
                        .status(ProjectStatus.Active)
                        .startDate(now)
                        .endDate(now.plusSeconds(86400))
                        .build();

        // When
        ProjectDTO projectDTO = projectMapper.toDto(project);

        // Then
        assertAll(
                () -> assertEquals(project.getId(), projectDTO.getId()),
                () -> assertEquals(project.getName(), projectDTO.getName()),
                () -> assertEquals(project.getDescription(), projectDTO.getDescription()),
                () -> assertEquals(project.getShortName(), projectDTO.getShortName()),
                () -> assertEquals(project.getTeam().getId(), projectDTO.getTeamId()),
                () -> assertEquals(project.getStatus(), projectDTO.getStatus()),
                () -> assertEquals(project.getStartDate(), projectDTO.getStartDate()),
                () -> assertEquals(project.getEndDate(), projectDTO.getEndDate()));
    }

    @Test
    public void testToEntity() {
        // Given
        Instant now = Instant.now();

        ProjectDTO projectDTO =
                ProjectDTO.builder()
                        .id(1L)
                        .name("Project Name")
                        .description("Project Description")
                        .shortName("PRJ")
                        .teamId(1L)
                        .status(ProjectStatus.Active)
                        .startDate(now)
                        .endDate(now.plusSeconds(86400))
                        .createdBy(2L)
                        .build();

        // When
        Project project = projectMapper.toEntity(projectDTO);

        // Then
        assertAll(
                () -> assertEquals(projectDTO.getId(), project.getId()),
                () -> assertEquals(projectDTO.getName(), project.getName()),
                () -> assertEquals(projectDTO.getDescription(), project.getDescription()),
                () -> assertEquals(projectDTO.getShortName(), project.getShortName()),
                () -> assertEquals(projectDTO.getTeamId(), project.getTeam().getId()),
                () -> assertEquals(projectDTO.getStatus(), project.getStatus()),
                () -> assertEquals(projectDTO.getStartDate(), project.getStartDate()),
                () -> assertEquals(projectDTO.getEndDate(), project.getEndDate()),
                () -> assertEquals(projectDTO.getCreatedBy(), project.getCreatedByUser().getId()));
    }
}
