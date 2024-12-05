package io.flexwork.modules.teams.service.mapper;

import io.flexwork.modules.teams.domain.WorkflowState;
import io.flexwork.modules.teams.service.dto.WorkflowStateDTO;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface WorkflowStateMapper {

    @Mapping(source = "workflow.id", target = "workflowId")
    WorkflowStateDTO toDto(WorkflowState workflowState);

    @Mapping(source = "workflowId", target = "workflow.id")
    WorkflowState toEntity(WorkflowStateDTO workflowStateDTO);

    List<WorkflowStateDTO> toDTOList(List<WorkflowState> states);
}
