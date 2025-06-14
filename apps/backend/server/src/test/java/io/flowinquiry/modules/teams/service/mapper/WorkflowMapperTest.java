package io.flowinquiry.modules.teams.service.mapper;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.flowinquiry.modules.teams.domain.Team;
import io.flowinquiry.modules.teams.domain.Workflow;
import io.flowinquiry.modules.teams.domain.WorkflowState;
import io.flowinquiry.modules.teams.domain.WorkflowTransition;
import io.flowinquiry.modules.teams.domain.WorkflowVisibility;
import io.flowinquiry.modules.teams.service.dto.WorkflowDTO;
import io.flowinquiry.modules.teams.service.dto.WorkflowDetailedDTO;
import io.flowinquiry.modules.usermanagement.domain.User;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

public class WorkflowMapperTest {

    private WorkflowMapper workflowMapper = Mappers.getMapper(WorkflowMapper.class);

    @Test
    public void testToDto() {
        // Given
        Instant now = Instant.now();
        Team team = Team.builder().id(1L).name("Team Name").build();
        User user =
                User.builder()
                        .id(2L)
                        .firstName("Test")
                        .lastName("User")
                        .email("test@example.com")
                        .build();

        Workflow workflow =
                Workflow.builder()
                        .id(1L)
                        .name("Workflow Name")
                        .description("Workflow Description")
                        .requestName("Request Name")
                        .owner(team)
                        .visibility(WorkflowVisibility.TEAM)
                        .level1EscalationTimeout(100)
                        .level2EscalationTimeout(200)
                        .level3EscalationTimeout(300)
                        .clonedFromGlobal(false)
                        .useForProject(true)
                        .tags("tag1,tag2")
                        .build();

        // Set audit fields
        workflow.setCreatedBy(user.getId());
        workflow.setCreatedByUser(user);
        workflow.setCreatedAt(now);
        workflow.setModifiedBy(user.getId());
        workflow.setModifiedByUser(user);
        workflow.setModifiedAt(now);

        // When
        WorkflowDTO workflowDTO = workflowMapper.toDto(workflow);

        // Then
        assertAll(
                () -> assertEquals(workflow.getId(), workflowDTO.getId()),
                () -> assertEquals(workflow.getName(), workflowDTO.getName()),
                () -> assertEquals(workflow.getDescription(), workflowDTO.getDescription()),
                () -> assertEquals(workflow.getRequestName(), workflowDTO.getRequestName()),
                () -> assertEquals(workflow.getOwner().getId(), workflowDTO.getOwnerId()),
                () -> assertEquals(workflow.getVisibility(), workflowDTO.getVisibility()),
                () ->
                        assertEquals(
                                workflow.getLevel1EscalationTimeout(),
                                workflowDTO.getLevel1EscalationTimeout()),
                () ->
                        assertEquals(
                                workflow.getLevel2EscalationTimeout(),
                                workflowDTO.getLevel2EscalationTimeout()),
                () ->
                        assertEquals(
                                workflow.getLevel3EscalationTimeout(),
                                workflowDTO.getLevel3EscalationTimeout()),
                () -> assertEquals(workflow.isUseForProject(), workflowDTO.isUseForProject()),
                () -> assertEquals(workflow.getTags(), workflowDTO.getTags()),
                // Audit fields
                () -> assertEquals(workflow.getCreatedBy(), workflowDTO.getCreatedBy()),
                () -> assertEquals(workflow.getCreatedAt(), workflowDTO.getCreatedAt()),
                () -> assertEquals(workflow.getModifiedBy(), workflowDTO.getModifiedBy()),
                () -> assertEquals(workflow.getModifiedAt(), workflowDTO.getModifiedAt()));
    }

    @Test
    public void testToEntity() {
        // Given
        WorkflowDTO workflowDTO =
                WorkflowDTO.builder()
                        .id(1L)
                        .name("Workflow Name")
                        .description("Workflow Description")
                        .requestName("Request Name")
                        .ownerId(1L)
                        .visibility(WorkflowVisibility.TEAM)
                        .level1EscalationTimeout(100)
                        .level2EscalationTimeout(200)
                        .level3EscalationTimeout(300)
                        .useForProject(true)
                        .tags("tag1,tag2")
                        .build();

        // When
        Workflow workflow = workflowMapper.toEntity(workflowDTO);

        // Then
        assertAll(
                () -> assertEquals(workflowDTO.getId(), workflow.getId()),
                () -> assertEquals(workflowDTO.getName(), workflow.getName()),
                () -> assertEquals(workflowDTO.getDescription(), workflow.getDescription()),
                () -> assertEquals(workflowDTO.getRequestName(), workflow.getRequestName()),
                () -> assertEquals(workflowDTO.getVisibility(), workflow.getVisibility()),
                () ->
                        assertEquals(
                                workflowDTO.getLevel1EscalationTimeout(),
                                workflow.getLevel1EscalationTimeout()),
                () ->
                        assertEquals(
                                workflowDTO.getLevel2EscalationTimeout(),
                                workflow.getLevel2EscalationTimeout()),
                () ->
                        assertEquals(
                                workflowDTO.getLevel3EscalationTimeout(),
                                workflow.getLevel3EscalationTimeout()),
                () -> assertEquals(workflowDTO.isUseForProject(), workflow.isUseForProject()),
                () -> assertEquals(workflowDTO.getTags(), workflow.getTags()));
    }

    @Test
    public void testToDetailedDto() {
        // Given
        Instant now = Instant.now();
        Team team = Team.builder().id(1L).name("Team Name").build();
        User user =
                User.builder()
                        .id(2L)
                        .firstName("Test")
                        .lastName("User")
                        .email("test@example.com")
                        .build();

        Set<WorkflowState> states = new HashSet<>();
        Set<WorkflowTransition> transitions = new HashSet<>();

        Workflow workflow =
                Workflow.builder()
                        .id(1L)
                        .name("Workflow Name")
                        .description("Workflow Description")
                        .requestName("Request Name")
                        .owner(team)
                        .visibility(WorkflowVisibility.TEAM)
                        .level1EscalationTimeout(100)
                        .level2EscalationTimeout(200)
                        .level3EscalationTimeout(300)
                        .clonedFromGlobal(false)
                        .useForProject(true)
                        .tags("tag1,tag2")
                        .states(states)
                        .transitions(transitions)
                        .build();

        // Set audit fields
        workflow.setCreatedBy(user.getId());
        workflow.setCreatedByUser(user);
        workflow.setCreatedAt(now);
        workflow.setModifiedBy(user.getId());
        workflow.setModifiedByUser(user);
        workflow.setModifiedAt(now);

        // When
        WorkflowDetailedDTO detailedDTO = workflowMapper.toDetailedDto(workflow);

        // Then
        assertAll(
                () -> assertEquals(workflow.getId(), detailedDTO.getId()),
                () -> assertEquals(workflow.getName(), detailedDTO.getName()),
                () -> assertEquals(workflow.getDescription(), detailedDTO.getDescription()),
                () -> assertEquals(workflow.getRequestName(), detailedDTO.getRequestName()),
                () -> assertEquals(workflow.getOwner().getId(), detailedDTO.getOwnerId()),
                () -> assertEquals(workflow.getOwner().getName(), detailedDTO.getOwnerName()),
                () -> assertEquals(workflow.getVisibility(), detailedDTO.getVisibility()),
                () ->
                        assertEquals(
                                workflow.getLevel1EscalationTimeout(),
                                detailedDTO.getLevel1EscalationTimeout()),
                () ->
                        assertEquals(
                                workflow.getLevel2EscalationTimeout(),
                                detailedDTO.getLevel2EscalationTimeout()),
                () ->
                        assertEquals(
                                workflow.getLevel3EscalationTimeout(),
                                detailedDTO.getLevel3EscalationTimeout()),
                () -> assertEquals(workflow.isUseForProject(), detailedDTO.isUseForProject()),
                () -> assertEquals(workflow.getTags(), detailedDTO.getTags()),
                // Verify states and transitions are mapped
                () -> assertEquals(workflow.getStates().size(), detailedDTO.getStates().size()),
                () ->
                        assertEquals(
                                workflow.getTransitions().size(),
                                detailedDTO.getTransitions().size()),
                // Audit fields
                () -> assertEquals(workflow.getCreatedBy(), detailedDTO.getCreatedBy()),
                () -> assertEquals(workflow.getCreatedAt(), detailedDTO.getCreatedAt()),
                () -> assertEquals(workflow.getModifiedBy(), detailedDTO.getModifiedBy()),
                () -> assertEquals(workflow.getModifiedAt(), detailedDTO.getModifiedAt()));
    }

    @Test
    public void testUpdateEntity() {
        // Given
        Team team = Team.builder().id(1L).name("Team Name").build();

        WorkflowDTO workflowDTO =
                WorkflowDTO.builder()
                        .id(1L)
                        .name("Updated Workflow Name")
                        .description("Updated Workflow Description")
                        .requestName("Updated Request Name")
                        .ownerId(1L)
                        .visibility(WorkflowVisibility.PUBLIC)
                        .level1EscalationTimeout(150)
                        .level2EscalationTimeout(250)
                        .level3EscalationTimeout(350)
                        .useForProject(false)
                        .tags("updatedTag1,updatedTag2")
                        .build();

        Workflow existingWorkflow =
                Workflow.builder()
                        .id(1L)
                        .name("Original Workflow Name")
                        .description("Original Workflow Description")
                        .requestName("Original Request Name")
                        .owner(team)
                        .visibility(WorkflowVisibility.TEAM)
                        .level1EscalationTimeout(100)
                        .level2EscalationTimeout(200)
                        .level3EscalationTimeout(300)
                        .clonedFromGlobal(false)
                        .useForProject(true)
                        .tags("tag1,tag2")
                        .build();

        // When
        workflowMapper.updateEntity(workflowDTO, existingWorkflow);

        // Then
        assertAll(
                () -> assertEquals(workflowDTO.getId(), existingWorkflow.getId()),
                () -> assertEquals(workflowDTO.getName(), existingWorkflow.getName()),
                () -> assertEquals(workflowDTO.getDescription(), existingWorkflow.getDescription()),
                () -> assertEquals(workflowDTO.getRequestName(), existingWorkflow.getRequestName()),
                () -> assertEquals(workflowDTO.getOwnerId(), existingWorkflow.getOwner().getId()),
                () -> assertEquals(workflowDTO.getVisibility(), existingWorkflow.getVisibility()),
                () ->
                        assertEquals(
                                workflowDTO.getLevel1EscalationTimeout(),
                                existingWorkflow.getLevel1EscalationTimeout()),
                () ->
                        assertEquals(
                                workflowDTO.getLevel2EscalationTimeout(),
                                existingWorkflow.getLevel2EscalationTimeout()),
                () ->
                        assertEquals(
                                workflowDTO.getLevel3EscalationTimeout(),
                                existingWorkflow.getLevel3EscalationTimeout()),
                () ->
                        assertEquals(
                                workflowDTO.isUseForProject(), existingWorkflow.isUseForProject()),
                () -> assertEquals(workflowDTO.getTags(), existingWorkflow.getTags()));
    }
}
