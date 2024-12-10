package io.flexwork.modules.teams.service.mapper;

import io.flexwork.modules.teams.domain.WorkflowTransition;
import io.flexwork.modules.teams.service.dto.WorkflowTransitionDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface WorkflowTransitionMapper {

    @Mapping(source = "sourceState.id", target = "sourceStateId")
    @Mapping(source = "targetState.id", target = "targetStateId")
    @Mapping(source = "workflow.id", target = "workflowId")
    WorkflowTransitionDTO toDto(WorkflowTransition transition);

    WorkflowTransition toEntity(WorkflowTransitionDTO transitionDTO);

    void updateEntity(
            WorkflowTransitionDTO transitionDTO,
            @MappingTarget WorkflowTransition workflowTransition);
}
