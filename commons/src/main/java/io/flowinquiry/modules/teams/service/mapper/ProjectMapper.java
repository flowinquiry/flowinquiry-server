package io.flowinquiry.modules.teams.service.mapper;

import io.flowinquiry.modules.teams.domain.Project;
import io.flowinquiry.modules.teams.service.dto.ProjectDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

    @Mapping(source = "team.id", target = "teamId")
    ProjectDTO toDto(Project project);

    @Mapping(source = "teamId", target = "team.id")
    Project toEntity(ProjectDTO projectDTO);
}
