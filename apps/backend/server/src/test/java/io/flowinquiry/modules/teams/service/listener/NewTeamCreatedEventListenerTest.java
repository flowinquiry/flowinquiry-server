package io.flowinquiry.modules.teams.service.listener;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.flowinquiry.exceptions.ResourceNotFoundException;
import io.flowinquiry.modules.teams.service.WorkflowService;
import io.flowinquiry.modules.teams.service.dto.TeamDTO;
import io.flowinquiry.modules.teams.service.dto.WorkflowDTO;
import io.flowinquiry.modules.teams.service.event.NewTeamCreatedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class NewTeamCreatedEventListenerTest {

    @Mock private WorkflowService workflowService;

    private NewTeamCreatedEventListener listener;

    @BeforeEach
    public void setup() {
        listener = new NewTeamCreatedEventListener(workflowService);
    }

    @Test
    public void testOnNewTeamCreated_ShouldCreateWorkflowByCloning() {
        // Given
        Long teamId = 1L;
        String teamName = "Test Team";

        TeamDTO teamDTO = TeamDTO.builder().id(teamId).name(teamName).build();

        NewTeamCreatedEvent event = new NewTeamCreatedEvent(this, teamDTO);

        Long globalWorkflowId = 100L;
        WorkflowDTO globalWorkflow =
                WorkflowDTO.builder()
                        .id(globalWorkflowId)
                        .name("Global Project Workflow")
                        .useForProject(true)
                        .build();

        when(workflowService.getGlobalWorkflowUsedForProject()).thenReturn(globalWorkflow);

        // When
        listener.onNewTeamCreated(event);

        // Then
        verify(workflowService).getGlobalWorkflowUsedForProject();

        ArgumentCaptor<WorkflowDTO> workflowCaptor = ArgumentCaptor.forClass(WorkflowDTO.class);
        verify(workflowService)
                .createWorkflowByCloning(
                        eq(teamId), eq(globalWorkflowId), workflowCaptor.capture());

        WorkflowDTO capturedWorkflow = workflowCaptor.getValue();
        assert capturedWorkflow.getName().equals("Project workflow");
        assert capturedWorkflow.getDescription().equals("Workflow for project management");
        assert capturedWorkflow.getRequestName().equals("Ticket");
        assert capturedWorkflow.isUseForProject();
    }

    @Test
    public void testOnNewTeamCreated_WhenGlobalWorkflowNotFound_ShouldThrowException() {
        // Given
        Long teamId = 1L;
        String teamName = "Test Team";

        TeamDTO teamDTO = TeamDTO.builder().id(teamId).name(teamName).build();

        NewTeamCreatedEvent event = new NewTeamCreatedEvent(this, teamDTO);

        when(workflowService.getGlobalWorkflowUsedForProject())
                .thenThrow(new ResourceNotFoundException("Global workflow for project not found"));

        // When & Then
        assertThrows(
                ResourceNotFoundException.class,
                () -> {
                    listener.onNewTeamCreated(event);
                });

        verify(workflowService).getGlobalWorkflowUsedForProject();
        verify(workflowService, never()).createWorkflowByCloning(any(), any(), any());
    }

    @Test
    public void testOnNewTeamCreated_WhenCreateWorkflowFails_ShouldThrowException() {
        // Given
        Long teamId = 1L;
        String teamName = "Test Team";

        TeamDTO teamDTO = TeamDTO.builder().id(teamId).name(teamName).build();

        NewTeamCreatedEvent event = new NewTeamCreatedEvent(this, teamDTO);

        Long globalWorkflowId = 100L;
        WorkflowDTO globalWorkflow =
                WorkflowDTO.builder()
                        .id(globalWorkflowId)
                        .name("Global Project Workflow")
                        .useForProject(true)
                        .build();

        when(workflowService.getGlobalWorkflowUsedForProject()).thenReturn(globalWorkflow);
        doThrow(new RuntimeException("Failed to create workflow"))
                .when(workflowService)
                .createWorkflowByCloning(eq(teamId), eq(globalWorkflowId), any(WorkflowDTO.class));

        // When & Then
        assertThrows(
                RuntimeException.class,
                () -> {
                    listener.onNewTeamCreated(event);
                });

        verify(workflowService).getGlobalWorkflowUsedForProject();
        verify(workflowService)
                .createWorkflowByCloning(eq(teamId), eq(globalWorkflowId), any(WorkflowDTO.class));
    }
}
