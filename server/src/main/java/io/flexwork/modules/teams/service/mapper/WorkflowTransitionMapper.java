package io.flexwork.modules.teams.service.mapper;

import io.flexwork.modules.teams.domain.WorkflowTransition;
import io.flexwork.modules.teams.service.dto.WorkflowTransitionDTO;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface WorkflowTransitionMapper {

    @Mapping(source = "sourceState.id", target = "sourceStateId")
    @Mapping(source = "targetState.id", target = "targetStateId")
    @Mapping(source = "workflow.id", target = "workflowId")
    WorkflowTransitionDTO toDTO(WorkflowTransition transition);

    WorkflowTransition toEntity(WorkflowTransitionDTO transitionDTO);

    List<WorkflowTransitionDTO> toDTOList(List<WorkflowTransition> transitions);
}
