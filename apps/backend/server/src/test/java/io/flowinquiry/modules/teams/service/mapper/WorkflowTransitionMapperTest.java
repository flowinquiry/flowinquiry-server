package io.flowinquiry.modules.teams.service.mapper;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import io.flowinquiry.modules.teams.domain.Workflow;
import io.flowinquiry.modules.teams.domain.WorkflowState;
import io.flowinquiry.modules.teams.domain.WorkflowTransition;
import io.flowinquiry.modules.teams.service.dto.WorkflowTransitionDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

public class WorkflowTransitionMapperTest {

    private WorkflowTransitionMapper workflowTransitionMapper;

    @BeforeEach
    public void setup() {
        workflowTransitionMapper = Mappers.getMapper(WorkflowTransitionMapper.class);
    }

    @Test
    public void testToDto() {
        // Given
        Workflow workflow = Workflow.builder().id(1L).name("Test Workflow").build();

        WorkflowState sourceState =
                WorkflowState.builder().id(2L).workflow(workflow).stateName("Source State").build();

        WorkflowState targetState =
                WorkflowState.builder().id(3L).workflow(workflow).stateName("Target State").build();

        WorkflowTransition transition =
                WorkflowTransition.builder()
                        .id(4L)
                        .workflow(workflow)
                        .sourceState(sourceState)
                        .targetState(targetState)
                        .eventName("Test Event")
                        .slaDuration(3600L)
                        .escalateOnViolation(true)
                        .build();

        // When
        WorkflowTransitionDTO transitionDTO = workflowTransitionMapper.toDto(transition);

        // Then
        assertAll(
                () -> assertEquals(transition.getId(), transitionDTO.getId()),
                () -> assertEquals(transition.getWorkflow().getId(), transitionDTO.getWorkflowId()),
                () ->
                        assertEquals(
                                transition.getSourceState().getId(),
                                transitionDTO.getSourceStateId()),
                () ->
                        assertEquals(
                                transition.getTargetState().getId(),
                                transitionDTO.getTargetStateId()),
                () -> assertEquals(transition.getEventName(), transitionDTO.getEventName()),
                () -> assertEquals(transition.getSlaDuration(), transitionDTO.getSlaDuration()),
                () ->
                        assertEquals(
                                transition.isEscalateOnViolation(),
                                transitionDTO.isEscalateOnViolation()));
    }

    @Test
    public void testToEntity() {
        // Given
        WorkflowTransitionDTO transitionDTO =
                WorkflowTransitionDTO.builder()
                        .id(4L)
                        .workflowId(1L)
                        .sourceStateId(2L)
                        .targetStateId(3L)
                        .eventName("Test Event")
                        .slaDuration(3600L)
                        .escalateOnViolation(true)
                        .build();

        // When
        WorkflowTransition transition = workflowTransitionMapper.toEntity(transitionDTO);

        // Then
        assertAll(
                () -> assertEquals(transitionDTO.getId(), transition.getId()),
                () -> assertEquals(transitionDTO.getEventName(), transition.getEventName()),
                () -> assertEquals(transitionDTO.getSlaDuration(), transition.getSlaDuration()),
                () ->
                        assertEquals(
                                transitionDTO.isEscalateOnViolation(),
                                transition.isEscalateOnViolation()));
    }

    @Test
    public void testUpdateEntity() {
        // Given
        Workflow workflow = Workflow.builder().id(1L).name("Test Workflow").build();

        WorkflowState sourceState =
                WorkflowState.builder().id(2L).workflow(workflow).stateName("Source State").build();

        WorkflowState targetState =
                WorkflowState.builder().id(3L).workflow(workflow).stateName("Target State").build();

        WorkflowTransition existingTransition =
                WorkflowTransition.builder()
                        .id(4L)
                        .workflow(workflow)
                        .sourceState(sourceState)
                        .targetState(targetState)
                        .eventName("Original Event")
                        .slaDuration(3600L)
                        .escalateOnViolation(false)
                        .build();

        WorkflowTransitionDTO updateDTO =
                WorkflowTransitionDTO.builder()
                        .id(4L)
                        .workflowId(1L)
                        .sourceStateId(2L)
                        .targetStateId(3L)
                        .eventName("Updated Event")
                        .slaDuration(7200L)
                        .escalateOnViolation(true)
                        .build();

        // When
        workflowTransitionMapper.updateEntity(updateDTO, existingTransition);

        // Then
        assertAll(
                () -> assertEquals(updateDTO.getId(), existingTransition.getId()),
                () ->
                        assertEquals(
                                updateDTO.getWorkflowId(),
                                existingTransition.getWorkflow().getId()),
                () ->
                        assertEquals(
                                updateDTO.getSourceStateId(),
                                existingTransition.getSourceState().getId()),
                () ->
                        assertEquals(
                                updateDTO.getTargetStateId(),
                                existingTransition.getTargetState().getId()),
                () -> assertEquals(updateDTO.getEventName(), existingTransition.getEventName()),
                () -> assertEquals(updateDTO.getSlaDuration(), existingTransition.getSlaDuration()),
                () ->
                        assertEquals(
                                updateDTO.isEscalateOnViolation(),
                                existingTransition.isEscalateOnViolation()));
    }

    @Test
    public void testNullValues() {
        // Test null entity
        assertNull(workflowTransitionMapper.toDto(null));

        // Test null DTO
        assertNull(workflowTransitionMapper.toEntity(null));
    }
}
