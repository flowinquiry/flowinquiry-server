package io.flowinquiry.modules.teams.service.mapper;

import io.flowinquiry.modules.teams.domain.ProjectEpic;
import io.flowinquiry.modules.teams.service.dto.ProjectEpicDTO;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface ProjectEpicMapper {

    @Mapping(source = "project.id", target = "projectId")
    ProjectEpicDTO toDto(ProjectEpic entity);

    @Mapping(source = "projectId", target = "project.id")
    ProjectEpic toEntity(ProjectEpicDTO dto);

    @Mapping(source = "projectId", target = "project.id")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(ProjectEpicDTO dto, @MappingTarget ProjectEpic entity);
}
