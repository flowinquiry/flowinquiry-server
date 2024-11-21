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

    @Mapping(target = "teamId", source = "team.id")
    @Mapping(target = "requestUserId", source = "requestUser.id")
    @Mapping(
            target = "requestUserName",
            expression =
                    "java(concatName(teamRequest.getRequestUser().getFirstName(), teamRequest.getRequestUser().getLastName()))")
    @Mapping(target = "assignUserId", source = "assignUser.id")
    @Mapping(
            target = "assignUserName",
            expression =
                    "java(concatName(teamRequest.getAssignUser().getFirstName(), teamRequest.getAssignUser().getLastName()))")
    @Mapping(target = "workflowId", source = "workflow.id")
    @Mapping(target = "workflowName", source = "workflow.name")
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

    default String concatName(String firstName, String lastName) {
        return firstName + " " + (lastName != null ? lastName : "");
    }

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(TeamRequestDTO dto, @MappingTarget TeamRequest entity);
}
