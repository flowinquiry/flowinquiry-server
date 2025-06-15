package io.flowinquiry.modules.teams.service.listener;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.flowinquiry.modules.collab.domain.ActivityLog;
import io.flowinquiry.modules.collab.domain.EntityType;
import io.flowinquiry.modules.collab.repository.ActivityLogRepository;
import io.flowinquiry.modules.teams.domain.Team;
import io.flowinquiry.modules.teams.domain.Ticket;
import io.flowinquiry.modules.teams.domain.WorkflowState;
import io.flowinquiry.modules.teams.repository.TicketRepository;
import io.flowinquiry.modules.teams.repository.WorkflowStateRepository;
import io.flowinquiry.modules.teams.service.WorkflowTransitionHistoryService;
import io.flowinquiry.modules.teams.service.event.TicketWorkStateTransitionEvent;
import io.flowinquiry.modules.usermanagement.domain.User;
import jakarta.persistence.EntityNotFoundException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TicketWorkStateTransitionEventListenerTest {

    @Mock private ActivityLogRepository activityLogRepository;

    @Mock private TicketRepository ticketRepository;

    @Mock private WorkflowStateRepository workflowStateRepository;

    @Mock private WorkflowTransitionHistoryService workflowTransitionHistoryService;

    private TicketWorkStateTransitionEventListener listener;

    @BeforeEach
    public void setup() {
        listener =
                new TicketWorkStateTransitionEventListener(
                        activityLogRepository,
                        ticketRepository,
                        workflowStateRepository,
                        workflowTransitionHistoryService);
    }

    @Test
    public void testOnWorkflowStateTransition_Success() {
        // Given
        Long ticketId = 1L;
        Long sourceStateId = 10L;
        Long targetStateId = 11L;
        Long teamId = 100L;
        Long userId = 1000L;

        TicketWorkStateTransitionEvent event =
                new TicketWorkStateTransitionEvent(this, ticketId, sourceStateId, targetStateId);

        WorkflowState sourceState =
                WorkflowState.builder().id(sourceStateId).stateName("In Progress").build();

        WorkflowState targetState =
                WorkflowState.builder().id(targetStateId).stateName("Done").build();

        Team team = Team.builder().id(teamId).name("Test Team").build();

        User user = User.builder().id(userId).firstName("John").lastName("Doe").build();

        Ticket ticket =
                Ticket.builder().id(ticketId).team(team).requestTitle("Test Ticket").build();
        ticket.setModifiedBy(userId);
        ticket.setModifiedByUser(user);

        when(workflowStateRepository.findById(sourceStateId)).thenReturn(Optional.of(sourceState));
        when(workflowStateRepository.findById(targetStateId)).thenReturn(Optional.of(targetState));
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));

        // When
        listener.onWorkflowStateTransition(event);

        // Then
        verify(workflowTransitionHistoryService)
                .recordWorkflowTransitionHistory(ticketId, sourceStateId, targetStateId);
        verify(workflowStateRepository).findById(sourceStateId);
        verify(workflowStateRepository).findById(targetStateId);
        verify(ticketRepository).findById(ticketId);

        ArgumentCaptor<ActivityLog> activityLogCaptor = ArgumentCaptor.forClass(ActivityLog.class);
        verify(activityLogRepository).save(activityLogCaptor.capture());

        ActivityLog capturedActivityLog = activityLogCaptor.getValue();
        assert capturedActivityLog.getEntityId().equals(teamId);
        assert capturedActivityLog.getEntityType().equals(EntityType.Team);
        assert capturedActivityLog.getContent().contains("John Doe");
        assert capturedActivityLog.getContent().contains("Test Ticket");
        assert capturedActivityLog.getContent().contains("In Progress");
        assert capturedActivityLog.getContent().contains("Done");
    }

    @Test
    public void testOnWorkflowStateTransition_SourceStateNotFound() {
        // Given
        Long ticketId = 1L;
        Long sourceStateId = 10L;
        Long targetStateId = 11L;

        TicketWorkStateTransitionEvent event =
                new TicketWorkStateTransitionEvent(this, ticketId, sourceStateId, targetStateId);

        when(workflowStateRepository.findById(sourceStateId)).thenReturn(Optional.empty());

        // When & Then
        try {
            listener.onWorkflowStateTransition(event);
        } catch (EntityNotFoundException e) {
            assert e.getMessage().contains("Can not find workflow state with id " + sourceStateId);
        }

        verify(workflowTransitionHistoryService)
                .recordWorkflowTransitionHistory(ticketId, sourceStateId, targetStateId);
        verify(workflowStateRepository).findById(sourceStateId);
    }

    @Test
    public void testOnWorkflowStateTransition_TargetStateNotFound() {
        // Given
        Long ticketId = 1L;
        Long sourceStateId = 10L;
        Long targetStateId = 11L;

        TicketWorkStateTransitionEvent event =
                new TicketWorkStateTransitionEvent(this, ticketId, sourceStateId, targetStateId);

        WorkflowState sourceState =
                WorkflowState.builder().id(sourceStateId).stateName("In Progress").build();

        when(workflowStateRepository.findById(sourceStateId)).thenReturn(Optional.of(sourceState));
        when(workflowStateRepository.findById(targetStateId)).thenReturn(Optional.empty());

        // When & Then
        try {
            listener.onWorkflowStateTransition(event);
        } catch (EntityNotFoundException e) {
            assert e.getMessage().contains("Can not find workflow state with id " + targetStateId);
        }

        verify(workflowTransitionHistoryService)
                .recordWorkflowTransitionHistory(ticketId, sourceStateId, targetStateId);
        verify(workflowStateRepository).findById(sourceStateId);
        verify(workflowStateRepository).findById(targetStateId);
    }

    @Test
    public void testOnWorkflowStateTransition_TicketNotFound() {
        // Given
        Long ticketId = 1L;
        Long sourceStateId = 10L;
        Long targetStateId = 11L;

        TicketWorkStateTransitionEvent event =
                new TicketWorkStateTransitionEvent(this, ticketId, sourceStateId, targetStateId);

        WorkflowState sourceState =
                WorkflowState.builder().id(sourceStateId).stateName("In Progress").build();

        WorkflowState targetState =
                WorkflowState.builder().id(targetStateId).stateName("Done").build();

        when(workflowStateRepository.findById(sourceStateId)).thenReturn(Optional.of(sourceState));
        when(workflowStateRepository.findById(targetStateId)).thenReturn(Optional.of(targetState));
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.empty());

        // When & Then
        try {
            listener.onWorkflowStateTransition(event);
        } catch (EntityNotFoundException e) {
            assert e.getMessage().contains("Can not find ticket with id " + ticketId);
        }

        verify(workflowTransitionHistoryService)
                .recordWorkflowTransitionHistory(ticketId, sourceStateId, targetStateId);
        verify(workflowStateRepository).findById(sourceStateId);
        verify(workflowStateRepository).findById(targetStateId);
        verify(ticketRepository).findById(ticketId);
    }
}
