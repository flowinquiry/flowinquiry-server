package io.flexwork.modules.teams.service.mapper;

import io.flexwork.modules.teams.domain.TeamRequest;
import io.flexwork.modules.teams.domain.Workflow;
import io.flexwork.modules.teams.service.dto.TeamRequestDTO;
import io.flexwork.modules.usermanagement.domain.Team;
import io.flexwork.modules.usermanagement.domain.User;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface TeamRequestMapper {

    TeamRequestDTO toDto(TeamRequest teamRequest);

    @Mapping(target = "team", source = "teamId", qualifiedByName = "toTeam")
    @Mapping(target = "workflow", source = "workflowId", qualifiedByName = "toWorkflow")
    @Mapping(target = "requestUser", source = "requestUserId", qualifiedByName = "toUser")
    @Mapping(target = "assignUser", source = "assignUserId", qualifiedByName = "toUser")
    TeamRequest toEntity(TeamRequestDTO teamRequestDTO);

    @Named("toTeam")
    default Team toTeam(Long teamId) {
        return (teamId == null) ? null : Team.builder().id(teamId).build();
    }

    @Named("toWorkflow")
    default Workflow toWorkflow(Long workflowId) {
        return (workflowId == null) ? null : Workflow.builder().id(workflowId).build();
    }

    @Named("toUser")
    default User toUser(Long userId) {
        return (userId == null) ? null : User.builder().id(userId).build();
    }

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(TeamRequestDTO dto, @MappingTarget TeamRequest entity);
}
