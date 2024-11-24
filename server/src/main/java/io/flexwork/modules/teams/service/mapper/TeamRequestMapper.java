package io.flexwork.modules.teams.service.mapper;

import io.flexwork.modules.collab.domain.Team;
import io.flexwork.modules.teams.domain.TeamRequest;
import io.flexwork.modules.teams.domain.Workflow;
import io.flexwork.modules.teams.service.dto.TeamRequestDTO;
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
            source = "requestUser",
            qualifiedByName = "mapUserFullName")
    @Mapping(target = "requestUserImageUrl", source = "requestUser.imageUrl")
    @Mapping(target = "assignUserId", source = "assignUser.id")
    @Mapping(target = "assignUserName", source = "assignUser", qualifiedByName = "mapUserFullName")
    @Mapping(target = "assignUserImageUrl", source = "assignUser.imageUrl")
    @Mapping(target = "teamName", source = "team.name")
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

    @Named("mapUserFullName")
    default String mapUserFullName(User user) {
        if (user == null) {
            return null;
        }
        String firstName = user.getFirstName() != null ? user.getFirstName() : "";
        String lastName = user.getLastName() != null ? user.getLastName() : "";
        return (firstName + " " + lastName).trim();
    }

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "assignUser", source = "assignUserId", qualifiedByName = "toUser")
    @Mapping(target = "requestUser", source = "requestUserId", qualifiedByName = "toUser")
    void updateEntity(TeamRequestDTO dto, @MappingTarget TeamRequest entity);
}
