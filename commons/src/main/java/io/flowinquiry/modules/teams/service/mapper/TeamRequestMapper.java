package io.flowinquiry.modules.teams.service.mapper;

import io.flowinquiry.modules.teams.domain.Team;
import io.flowinquiry.modules.teams.domain.TeamRequest;
import io.flowinquiry.modules.teams.domain.Workflow;
import io.flowinquiry.modules.teams.domain.WorkflowState;
import io.flowinquiry.modules.teams.service.dto.TeamRequestDTO;
import io.flowinquiry.modules.teams.service.dto.WatcherDTO;
import io.flowinquiry.modules.usermanagement.domain.User;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper(
        componentModel = "spring",
        uses = {TeamRequestConversationHealthMapper.class})
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
    @Mapping(target = "workflowRequestName", source = "workflow.requestName")
    @Mapping(target = "currentStateId", source = "currentState.id")
    @Mapping(target = "currentStateName", source = "currentState.stateName")
    @Mapping(target = "conversationHealth", source = "conversationHealth")
    TeamRequestDTO toDto(TeamRequest teamRequest);

    @Mapping(target = "team", source = "teamId", qualifiedByName = "toTeam")
    @Mapping(target = "workflow", source = "workflowId", qualifiedByName = "toWorkflow")
    @Mapping(target = "requestUser", source = "requestUserId", qualifiedByName = "toUser")
    @Mapping(target = "assignUser", source = "assignUserId", qualifiedByName = "toUser")
    @Mapping(
            target = "currentState",
            source = "currentStateId",
            qualifiedByName = "toWorkflowState")
    @Mapping(target = "conversationHealth", source = "conversationHealth")
    TeamRequest toEntity(TeamRequestDTO teamRequestDTO);

    @Mapping(target = "team", source = "teamId", qualifiedByName = "toTeam")
    @Mapping(target = "workflow", source = "workflowId", qualifiedByName = "toWorkflow")
    @Mapping(target = "assignUser", source = "assignUserId", qualifiedByName = "toUser")
    @Mapping(target = "requestUser", source = "requestUserId", qualifiedByName = "toUser")
    @Mapping(
            target = "currentState",
            source = "currentStateId",
            qualifiedByName = "toWorkflowState")
    @Mapping(
            target = "conversationHealth",
            ignore = true) // ✅ Ignore to avoid detached entity issue
    void updateEntity(TeamRequestDTO dto, @MappingTarget TeamRequest entity);

    @Named("toTeam")
    default Team toTeam(Long teamId) {
        return (teamId == null) ? null : Team.builder().id(teamId).build();
    }

    @Named("toWorkflow")
    default Workflow toWorkflow(Long workflowId) {
        return (workflowId == null) ? null : Workflow.builder().id(workflowId).build();
    }

    @Named("toWorkflowState")
    default WorkflowState toWorkflowState(Long workflowStateId) {
        return (workflowStateId == null)
                ? null
                : WorkflowState.builder().id(workflowStateId).build();
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

    default Set<WatcherDTO> mapWatchers(Set<User> watchers) {
        if (watchers == null || watchers.isEmpty()) {
            return Collections.emptySet();
        }
        WatcherMapper watcherMapper = Mappers.getMapper(WatcherMapper.class);
        return watchers.stream().map(watcherMapper::toDto).collect(Collectors.toSet());
    }
}
