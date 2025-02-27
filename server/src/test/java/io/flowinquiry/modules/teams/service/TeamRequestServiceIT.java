package io.flowinquiry.modules.teams.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import io.flowinquiry.IntegrationTest;
import io.flowinquiry.modules.audit.AuditLogUpdateEvent;
import io.flowinquiry.modules.collab.domain.EntityType;
import io.flowinquiry.modules.collab.domain.EntityWatcher;
import io.flowinquiry.modules.collab.repository.EntityWatcherRepository;
import io.flowinquiry.modules.teams.domain.TicketChannel;
import io.flowinquiry.modules.teams.repository.TeamRequestRepository;
import io.flowinquiry.modules.teams.service.dto.TeamRequestDTO;
import io.flowinquiry.modules.teams.service.event.NewTeamRequestCreatedEvent;
import io.flowinquiry.modules.teams.service.event.TeamRequestWorkStateTransitionEvent;
import io.flowinquiry.modules.teams.service.mapper.TeamRequestMapper;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@Transactional
public class TeamRequestServiceIT {

    @Autowired private TeamRequestService teamRequestService;
    @Autowired private TeamRequestRepository teamRequestRepository;
    @Autowired private TeamRequestMapper teamRequestMapper;
    @Autowired private EntityWatcherRepository entityWatcherRepository;
    @Autowired private ApplicationEventPublisher realEventPublisher;
    private ApplicationEventPublisher spyEventPublisher;

    @BeforeEach
    void setUp() {
        spyEventPublisher = Mockito.spy(realEventPublisher);
        ReflectionTestUtils.setField(teamRequestService, "eventPublisher", spyEventPublisher);
        doNothing().when(spyEventPublisher).publishEvent(any());
    }

    @Test
    void shouldCreateTeamRequestSuccessfully() {
        TeamRequestDTO teamRequestDTO =
                teamRequestMapper.toDto(teamRequestRepository.findById(2L).orElseThrow());
        teamRequestDTO.setId(null);
        teamRequestDTO.setConversationHealth(null);
        TeamRequestDTO savedTeamRequest = teamRequestService.createTeamRequest(teamRequestDTO);
        assertThat(savedTeamRequest).isNotNull();

        ArgumentCaptor<NewTeamRequestCreatedEvent> eventCaptor =
                ArgumentCaptor.forClass(NewTeamRequestCreatedEvent.class);
        verify(spyEventPublisher, times(1)).publishEvent(eventCaptor.capture());
    }

    @Test
    void shouldUpdateTeamRequestSuccessfully() {
        TeamRequestDTO teamRequestDTO =
                teamRequestMapper.toDto(teamRequestRepository.findById(1L).orElseThrow());
        teamRequestDTO.setRequestTitle("Updated Request Title");
        teamRequestDTO.setCurrentStateId(2L);

        TeamRequestDTO updatedRequest = teamRequestService.updateTeamRequest(teamRequestDTO);

        assertThat(updatedRequest.getRequestTitle()).isEqualTo("Updated Request Title");
        assertThat(updatedRequest.getCurrentStateId()).isEqualTo(2L);

        List<EntityWatcher> watchers =
                entityWatcherRepository.findByEntityTypeAndEntityId(EntityType.Team_Request, 1L);
        assertThat(watchers).hasSize(3);
        List<String> emails =
                watchers.stream().map(watcher -> watcher.getWatchUser().getEmail()).toList();
        assertThat(emails)
                .containsExactlyInAnyOrder(
                        "alice.johnson@flowinquiry.io",
                        "charlie.green@flowinquiry.io",
                        "bob.brown@flowinquiry.io");

        ArgumentCaptor<AuditLogUpdateEvent> auditLogUpdateEventArgumentCaptor =
                ArgumentCaptor.forClass(AuditLogUpdateEvent.class);
        verify(spyEventPublisher, times(1))
                .publishEvent(auditLogUpdateEventArgumentCaptor.capture());

        ArgumentCaptor<TeamRequestWorkStateTransitionEvent> workflowStateTransitionEventCaptor =
                ArgumentCaptor.forClass(TeamRequestWorkStateTransitionEvent.class);
        verify(spyEventPublisher, times(1))
                .publishEvent(workflowStateTransitionEventCaptor.capture());

        TeamRequestWorkStateTransitionEvent capturedEvent =
                workflowStateTransitionEventCaptor.getValue();

        assertThat(capturedEvent.getSourceStateId()).isEqualTo(1L);
        assertThat(capturedEvent.getTargetStateId()).isEqualTo(2L);
    }

    @Test
    void shouldFindNextTeamRequestSuccessfully() {
        TeamRequestDTO nextEntity = teamRequestService.getNextEntity(11L).orElseThrow();

        // Then: Validate key properties in a single assertion block
        assertThat(nextEntity)
                .isNotNull()
                .extracting(
                        TeamRequestDTO::getId,
                        TeamRequestDTO::getTeamId,
                        TeamRequestDTO::getWorkflowId,
                        TeamRequestDTO::getRequestTitle,
                        TeamRequestDTO::getRequestDescription,
                        TeamRequestDTO::getChannel,
                        TeamRequestDTO::getIsNew,
                        TeamRequestDTO::getIsCompleted,
                        TeamRequestDTO::getEstimatedCompletionDate)
                .containsExactly(
                        12L,
                        1L,
                        1L, // ID, team, workflow
                        "Refund Status Inquiry",
                        "Customer wants an update on refund.",
                        TicketChannel.CHAT,
                        true,
                        false,
                        LocalDate.of(2025, 2, 17));

        assertThat(nextEntity.getCreatedAt()).isNotNull();
        assertThat(nextEntity.getModifiedAt()).isNotNull();
        assertThat(nextEntity.getNumberAttachments()).isGreaterThanOrEqualTo(0);
    }

    @Test
    void shouldFindPreviousTeamRequestSuccessfully() {
        TeamRequestDTO previousEntity = teamRequestService.getPreviousEntity(11L).orElseThrow();

        // Then: Validate key properties in a single assertion block
        assertThat(previousEntity)
                .isNotNull()
                .extracting(
                        TeamRequestDTO::getId,
                        TeamRequestDTO::getTeamId,
                        TeamRequestDTO::getWorkflowId,
                        TeamRequestDTO::getRequestTitle,
                        TeamRequestDTO::getRequestDescription,
                        TeamRequestDTO::getChannel,
                        TeamRequestDTO::getIsNew,
                        TeamRequestDTO::getIsCompleted,
                        TeamRequestDTO::getEstimatedCompletionDate)
                .containsExactly(
                        1L,
                        1L,
                        1L, // ID, team, workflow
                        "Customer Refund Issue",
                        "Customer reported an issue with a refund request.",
                        TicketChannel.WEB_PORTAL,
                        true,
                        false,
                        LocalDate.of(2025, 2, 15));

        assertThat(previousEntity.getCreatedAt()).isNotNull();
        assertThat(previousEntity.getModifiedAt()).isNotNull();
        assertThat(previousEntity.getNumberAttachments()).isGreaterThanOrEqualTo(0);
    }
}
