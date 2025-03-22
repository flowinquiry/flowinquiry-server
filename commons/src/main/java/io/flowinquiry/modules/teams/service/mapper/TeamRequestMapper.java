package io.flowinquiry.modules.teams.service.mapper;

import io.flowinquiry.modules.teams.domain.Team;
import io.flowinquiry.modules.teams.domain.TeamRequest;
import io.flowinquiry.modules.teams.service.dto.TeamRequestDTO;
import io.flowinquiry.modules.usermanagement.domain.User;
import io.flowinquiry.utils.BaseMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(
        componentModel = "spring",
        uses = {TeamRequestConversationHealthMapper.class})
public interface TeamRequestMapper extends BaseMapper {

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
    @Mapping(target = "projectId", source = "project.id")
    @Mapping(target = "projectName", source = "project.name")
    @Mapping(target = "currentStateId", source = "currentState.id")
    @Mapping(target = "currentStateName", source = "currentState.stateName")
    @Mapping(target = "iterationId", source = "iteration.id")
    @Mapping(target = "iterationName", source = "iteration.name")
    @Mapping(target = "epicId", source = "epic.id")
    @Mapping(target = "epicName", source = "epic.name")
    @Mapping(target = "conversationHealth", source = "conversationHealth")
    TeamRequestDTO toDto(TeamRequest teamRequest);

    @Mapping(target = "team", source = "teamId", qualifiedByName = "toTeam")
    @Mapping(
            target = "workflow",
            expression = "java(toStub(teamRequestDTO.getWorkflowId(), Workflow.class))")
    @Mapping(
            target = "project",
            expression = "java(toStub(teamRequestDTO.getProjectId(), Project.class))")
    @Mapping(
            target = "requestUser",
            expression = "java(toStub(teamRequestDTO.getRequestUserId(), User.class))")
    @Mapping(
            target = "assignUser",
            expression = "java(toStub(teamRequestDTO.getAssignUserId(), User.class))")
    @Mapping(
            target = "iteration",
            expression = "java(toStub(teamRequestDTO.getIterationId(), ProjectIteration.class))")
    @Mapping(
            target = "epic",
            expression = "java(toStub(teamRequestDTO.getEpicId(), ProjectEpic.class))")
    @Mapping(
            target = "currentState",
            expression = "java(toStub(teamRequestDTO.getCurrentStateId(), WorkflowState.class))")
    @Mapping(target = "conversationHealth", source = "conversationHealth")
    TeamRequest toEntity(TeamRequestDTO teamRequestDTO);

    @Mapping(target = "team", source = "teamId", qualifiedByName = "toTeam")
    @Mapping(target = "workflow", expression = "java(toStub(dto.getWorkflowId(), Workflow.class))")
    @Mapping(
            target = "requestUser",
            expression = "java(toStub(dto.getRequestUserId(), User.class))")
    @Mapping(target = "assignUser", expression = "java(toStub(dto.getAssignUserId(), User.class))")
    @Mapping(
            target = "iteration",
            expression = "java(toStub(dto.getIterationId(), ProjectIteration.class))")
    @Mapping(target = "epic", expression = "java(toStub(dto.getEpicId(), ProjectEpic.class))")
    @Mapping(
            target = "currentState",
            expression = "java(toStub(dto.getCurrentStateId(), WorkflowState.class))")
    @Mapping(target = "project", expression = "java(toStub(dto.getProjectId(), Project.class))")
    @Mapping(
            target = "conversationHealth",
            ignore = true) // ✅ Ignore to avoid detached entity issue
    void updateEntity(TeamRequestDTO dto, @MappingTarget TeamRequest entity);

    @Named("toTeam")
    default Team toTeam(Long teamId) {
        return (teamId == null) ? null : Team.builder().id(teamId).build();
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
}
