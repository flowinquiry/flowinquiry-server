package io.flexwork.modules.teams.service.mapper;

import io.flexwork.modules.teams.domain.Workflow;
import io.flexwork.modules.teams.service.dto.WorkflowDTO;
import io.flexwork.modules.teams.service.dto.WorkflowDetailedDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        componentModel = "spring",
        uses = {WorkflowStateMapper.class, WorkflowTransitionMapper.class})
public interface WorkflowMapper {

    @Mapping(source = "owner.id", target = "ownerId")
    WorkflowDTO toDto(Workflow workflow);

    Workflow toEntity(WorkflowDTO workflowDTO);

    @Mapping(source = "owner.name", target = "ownerName")
    @Mapping(source = "owner.id", target = "ownerId")
    WorkflowDetailedDTO toDetailedDTO(Workflow workflow);
}
