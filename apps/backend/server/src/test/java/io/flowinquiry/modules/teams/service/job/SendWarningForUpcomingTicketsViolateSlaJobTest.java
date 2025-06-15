package io.flowinquiry.modules.teams.service.job;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.flowinquiry.modules.collab.domain.Notification;
import io.flowinquiry.modules.collab.domain.NotificationType;
import io.flowinquiry.modules.shared.service.cache.DeduplicationCacheService;
import io.flowinquiry.modules.teams.domain.Team;
import io.flowinquiry.modules.teams.domain.Ticket;
import io.flowinquiry.modules.teams.domain.Workflow;
import io.flowinquiry.modules.teams.domain.WorkflowState;
import io.flowinquiry.modules.teams.domain.WorkflowTransitionHistory;
import io.flowinquiry.modules.teams.domain.WorkflowTransitionHistoryStatus;
import io.flowinquiry.modules.teams.service.TeamService;
import io.flowinquiry.modules.teams.service.WorkflowTransitionHistoryService;
import io.flowinquiry.modules.usermanagement.domain.User;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

/** Tests for {@link SendWarningForUpcomingTicketsViolateSlaJob}. */
@ExtendWith(MockitoExtension.class)
public class SendWarningForUpcomingTicketsViolateSlaJobTest {

    @Mock private SimpMessagingTemplate messageTemplate;

    @Mock private TeamService teamService;

    @Mock private WorkflowTransitionHistoryService workflowTransitionHistoryService;

    @Mock private DeduplicationCacheService deduplicationCacheService;

    private SendWarningForUpcomingTicketsViolateSlaJob job;

    // The threshold used in the job (30 minutes in seconds)
    private static final int PRIOR_SLA_WARNING_THRESHOLD_IN_SECONDS = 1800;

    @BeforeEach
    public void setup() {
        job =
                new SendWarningForUpcomingTicketsViolateSlaJob(
                        messageTemplate,
                        teamService,
                        workflowTransitionHistoryService,
                        deduplicationCacheService);
    }

    @Test
    public void testRunWithUpcomingViolatingTickets() {
        // Given
        WorkflowState fromState = WorkflowState.builder().id(1L).stateName("From State").build();

        WorkflowState toState = WorkflowState.builder().id(2L).stateName("To State").build();

        Workflow workflow = Workflow.builder().id(3L).build();

        Team team = Team.builder().id(4L).name("Test Team").build();

        User assignUser =
                User.builder()
                        .id(5L)
                        .firstName("John")
                        .lastName("Doe")
                        .email("john.doe@example.com")
                        .build();

        Ticket ticket =
                Ticket.builder()
                        .id(6L)
                        .requestTitle("Test Ticket")
                        .team(team)
                        .assignUser(assignUser)
                        .workflow(workflow)
                        .build();

        Instant now = Instant.now();
        // SLA due date is 25 minutes in the future (within the 30-minute warning threshold)
        Instant slaDueDate = now.plusSeconds(1500);

        WorkflowTransitionHistory upcomingViolatingTicket =
                WorkflowTransitionHistory.builder()
                        .id(7L)
                        .ticket(ticket)
                        .fromState(fromState)
                        .toState(toState)
                        .eventName("Test Event")
                        .transitionDate(now.minusSeconds(3600)) // 1 hour ago
                        .slaDueDate(slaDueDate)
                        .status(WorkflowTransitionHistoryStatus.In_Progress)
                        .build();

        List<WorkflowTransitionHistory> upcomingViolatingTickets = List.of(upcomingViolatingTicket);

        // Mock service calls
        when(workflowTransitionHistoryService.getViolatingTransitions(
                        PRIOR_SLA_WARNING_THRESHOLD_IN_SECONDS))
                .thenReturn(upcomingViolatingTickets);
        when(deduplicationCacheService.containsKey(anyString())).thenReturn(false);

        // When
        job.run();

        // Then
        // Verify notification sent to assigned user
        ArgumentCaptor<String> userIdCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Notification> notificationCaptor =
                ArgumentCaptor.forClass(Notification.class);

        verify(messageTemplate)
                .convertAndSendToUser(
                        userIdCaptor.capture(),
                        eq("/queue/notifications"),
                        notificationCaptor.capture());

        String capturedUserId = userIdCaptor.getValue();
        Notification capturedNotification = notificationCaptor.getValue();

        // Verify notification properties
        assert capturedUserId.equals(String.valueOf(assignUser.getId()));
        assert capturedNotification.getType() == NotificationType.SLA_WARNING;
        assert capturedNotification.getContent().contains("Test Ticket");
        assert capturedNotification.getContent().contains("approaching its SLA deadline");
        assert !capturedNotification.getIsRead();

        // Verify deduplication cache entry
        verify(deduplicationCacheService).put(anyString(), any(Duration.class));
    }

    @Test
    public void testRunWithNoUpcomingViolatingTickets() {
        // Given
        when(workflowTransitionHistoryService.getViolatingTransitions(
                        PRIOR_SLA_WARNING_THRESHOLD_IN_SECONDS))
                .thenReturn(Collections.emptyList());

        // When
        job.run();

        // Then
        verify(messageTemplate, never()).convertAndSendToUser(anyString(), anyString(), any());
        verify(deduplicationCacheService, never()).put(anyString(), any(Duration.class));
    }

    @Test
    public void testRunWithNullAssignUser() {
        // Given
        WorkflowState fromState = WorkflowState.builder().id(1L).stateName("From State").build();

        WorkflowState toState = WorkflowState.builder().id(2L).stateName("To State").build();

        Workflow workflow = Workflow.builder().id(3L).build();

        Team team = Team.builder().id(4L).name("Test Team").build();

        // Ticket with null assignUser
        Ticket ticket =
                Ticket.builder()
                        .id(6L)
                        .requestTitle("Test Ticket")
                        .team(team)
                        .assignUser(null) // No assigned user
                        .workflow(workflow)
                        .build();

        Instant now = Instant.now();
        // SLA due date is 25 minutes in the future (within the 30-minute warning threshold)
        Instant slaDueDate = now.plusSeconds(1500);

        WorkflowTransitionHistory upcomingViolatingTicket =
                WorkflowTransitionHistory.builder()
                        .id(7L)
                        .ticket(ticket)
                        .fromState(fromState)
                        .toState(toState)
                        .eventName("Test Event")
                        .transitionDate(now.minusSeconds(3600)) // 1 hour ago
                        .slaDueDate(slaDueDate)
                        .status(WorkflowTransitionHistoryStatus.In_Progress)
                        .build();

        List<WorkflowTransitionHistory> upcomingViolatingTickets = List.of(upcomingViolatingTicket);

        User teamManager =
                User.builder()
                        .id(8L)
                        .firstName("Manager")
                        .lastName("User")
                        .email("manager@example.com")
                        .build();

        List<User> teamManagers = List.of(teamManager);

        // Mock service calls
        when(workflowTransitionHistoryService.getViolatingTransitions(
                        PRIOR_SLA_WARNING_THRESHOLD_IN_SECONDS))
                .thenReturn(upcomingViolatingTickets);
        when(teamService.getTeamManagers(team.getId())).thenReturn(teamManagers);
        when(deduplicationCacheService.containsKey(anyString())).thenReturn(false);

        // When
        job.run();

        // Then
        // Verify notification sent to team manager
        verify(messageTemplate)
                .convertAndSendToUser(
                        eq(String.valueOf(teamManager.getId())),
                        eq("/queue/notifications"),
                        any(Notification.class));

        // Verify deduplication cache entry
        verify(deduplicationCacheService).put(anyString(), any(Duration.class));
    }

    @Test
    public void testRunWithDuplicateNotification() {
        // Given
        WorkflowState fromState = WorkflowState.builder().id(1L).stateName("From State").build();

        WorkflowState toState = WorkflowState.builder().id(2L).stateName("To State").build();

        Workflow workflow = Workflow.builder().id(3L).build();

        Team team = Team.builder().id(4L).name("Test Team").build();

        User assignUser =
                User.builder()
                        .id(5L)
                        .firstName("John")
                        .lastName("Doe")
                        .email("john.doe@example.com")
                        .build();

        Ticket ticket =
                Ticket.builder()
                        .id(6L)
                        .requestTitle("Test Ticket")
                        .team(team)
                        .assignUser(assignUser)
                        .workflow(workflow)
                        .build();

        Instant now = Instant.now();
        // SLA due date is 25 minutes in the future (within the 30-minute warning threshold)
        Instant slaDueDate = now.plusSeconds(1500);

        WorkflowTransitionHistory upcomingViolatingTicket =
                WorkflowTransitionHistory.builder()
                        .id(7L)
                        .ticket(ticket)
                        .fromState(fromState)
                        .toState(toState)
                        .eventName("Test Event")
                        .transitionDate(now.minusSeconds(3600)) // 1 hour ago
                        .slaDueDate(slaDueDate)
                        .status(WorkflowTransitionHistoryStatus.In_Progress)
                        .build();

        List<WorkflowTransitionHistory> upcomingViolatingTickets = List.of(upcomingViolatingTicket);

        // Mock service calls
        when(workflowTransitionHistoryService.getViolatingTransitions(
                        PRIOR_SLA_WARNING_THRESHOLD_IN_SECONDS))
                .thenReturn(upcomingViolatingTickets);

        // Simulate that notifications have already been sent (keys exist in cache)
        when(deduplicationCacheService.containsKey(anyString())).thenReturn(true);

        // When
        job.run();

        // Then
        // Verify no notifications are sent due to deduplication
        verify(messageTemplate, never()).convertAndSendToUser(anyString(), anyString(), any());
        verify(deduplicationCacheService, never()).put(anyString(), any(Duration.class));
    }
}
