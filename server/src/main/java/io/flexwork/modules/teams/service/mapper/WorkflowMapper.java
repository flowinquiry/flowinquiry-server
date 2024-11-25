package io.flexwork.modules.teams.service.mapper;

import io.flexwork.modules.teams.domain.Team;
import io.flexwork.modules.teams.domain.Workflow;
import io.flexwork.modules.teams.service.dto.WorkflowDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface WorkflowMapper {

    @Mapping(source = "owner", target = "global", qualifiedByName = "mapIsGlobal")
    WorkflowDTO toDto(Workflow workflow);

    Workflow toEntity(WorkflowDTO workflowDTO);

    @Named("mapIsGlobal")
    default boolean mapIsGlobal(Team owner) {
        return owner == null; // If owner is null, it's a global workflow
    }
}
