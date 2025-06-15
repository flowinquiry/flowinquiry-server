package io.flowinquiry.modules.teams.service.mapper;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import io.flowinquiry.modules.teams.domain.Workflow;
import io.flowinquiry.modules.teams.domain.WorkflowState;
import io.flowinquiry.modules.teams.service.dto.WorkflowStateDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

public class WorkflowStateMapperTest {

    private WorkflowStateMapper workflowStateMapper;

    @BeforeEach
    public void setup() {
        workflowStateMapper = Mappers.getMapper(WorkflowStateMapper.class);
    }

    @Test
    public void testToDto() {
        // Given
        Workflow workflow = Workflow.builder().id(1L).name("Test Workflow").build();

        WorkflowState state =
                WorkflowState.builder()
                        .id(2L)
                        .workflow(workflow)
                        .stateName("Test State")
                        .isInitial(true)
                        .isFinal(false)
                        .build();

        // When
        WorkflowStateDTO stateDTO = workflowStateMapper.toDto(state);

        // Then
        assertAll(
                () -> assertEquals(state.getId(), stateDTO.getId()),
                () -> assertEquals(state.getWorkflow().getId(), stateDTO.getWorkflowId()),
                () -> assertEquals(state.getStateName(), stateDTO.getStateName()),
                () -> assertEquals(state.getIsInitial(), stateDTO.getIsInitial()),
                () -> assertEquals(state.getIsFinal(), stateDTO.getIsFinal()));
    }

    @Test
    public void testToEntity() {
        // Given
        WorkflowStateDTO stateDTO =
                WorkflowStateDTO.builder()
                        .id(2L)
                        .workflowId(1L)
                        .stateName("Test State")
                        .isInitial(true)
                        .isFinal(false)
                        .build();

        // When
        WorkflowState state = workflowStateMapper.toEntity(stateDTO);

        // Then
        assertAll(
                () -> assertEquals(stateDTO.getId(), state.getId()),
                () -> assertEquals(stateDTO.getWorkflowId(), state.getWorkflow().getId()),
                () -> assertEquals(stateDTO.getStateName(), state.getStateName()),
                () -> assertEquals(stateDTO.getIsInitial(), state.getIsInitial()),
                () -> assertEquals(stateDTO.getIsFinal(), state.getIsFinal()));
    }

    @Test
    public void testUpdateEntity() {
        // Given
        Workflow workflow = Workflow.builder().id(1L).name("Test Workflow").build();

        WorkflowState existingState =
                WorkflowState.builder()
                        .id(2L)
                        .workflow(workflow)
                        .stateName("Original State")
                        .isInitial(true)
                        .isFinal(false)
                        .build();

        WorkflowStateDTO updateDTO =
                WorkflowStateDTO.builder()
                        .id(2L)
                        .workflowId(1L)
                        .stateName("Updated State")
                        .isInitial(false)
                        .isFinal(true)
                        .build();

        // When
        workflowStateMapper.updateEntity(updateDTO, existingState);

        // Then
        assertAll(
                () -> assertEquals(updateDTO.getId(), existingState.getId()),
                () -> assertEquals(updateDTO.getWorkflowId(), existingState.getWorkflow().getId()),
                () -> assertEquals(updateDTO.getStateName(), existingState.getStateName()),
                () -> assertEquals(updateDTO.getIsInitial(), existingState.getIsInitial()),
                () -> assertEquals(updateDTO.getIsFinal(), existingState.getIsFinal()));
    }

    @Test
    public void testNullValues() {
        // Test null entity
        assertNull(workflowStateMapper.toDto(null));

        // Test null DTO
        assertNull(workflowStateMapper.toEntity(null));
    }
}
