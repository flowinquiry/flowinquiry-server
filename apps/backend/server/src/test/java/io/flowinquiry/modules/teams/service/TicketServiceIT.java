package io.flowinquiry.modules.teams.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import io.flowinquiry.IntegrationTest;
import io.flowinquiry.modules.audit.service.event.AuditLogUpdateEvent;
import io.flowinquiry.modules.collab.domain.EntityType;
import io.flowinquiry.modules.collab.domain.EntityWatcher;
import io.flowinquiry.modules.collab.repository.EntityWatcherRepository;
import io.flowinquiry.modules.teams.domain.TShirtSize;
import io.flowinquiry.modules.teams.domain.TicketChannel;
import io.flowinquiry.modules.teams.repository.TicketRepository;
import io.flowinquiry.modules.teams.service.dto.TicketDTO;
import io.flowinquiry.modules.teams.service.event.NewTicketCreatedEvent;
import io.flowinquiry.modules.teams.service.event.TicketWorkStateTransitionEvent;
import io.flowinquiry.modules.teams.service.mapper.TicketMapper;
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
public class TicketServiceIT {

    @Autowired private TicketService ticketService;
    @Autowired private TicketRepository ticketRepository;
    @Autowired private TicketMapper ticketMapper;
    @Autowired private EntityWatcherRepository entityWatcherRepository;
    @Autowired private ApplicationEventPublisher realEventPublisher;
    private ApplicationEventPublisher spyEventPublisher;

    @BeforeEach
    void setUp() {
        spyEventPublisher = Mockito.spy(realEventPublisher);
        ReflectionTestUtils.setField(ticketService, "eventPublisher", spyEventPublisher);
        doNothing().when(spyEventPublisher).publishEvent(any());
    }

    @Test
    void shouldCreateTicketSuccessfully() {
        TicketDTO ticketDTO = ticketMapper.toDto(ticketRepository.findById(2L).orElseThrow());
        ticketDTO.setId(null);
        ticketDTO.setConversationHealth(null);
        TicketDTO savedTicket = ticketService.createTicket(ticketDTO);
        assertThat(savedTicket).isNotNull();

        ArgumentCaptor<NewTicketCreatedEvent> eventCaptor =
                ArgumentCaptor.forClass(NewTicketCreatedEvent.class);
        verify(spyEventPublisher, times(1)).publishEvent(eventCaptor.capture());
    }

    @Test
    void shouldUpdateTicketSuccessfully() {
        TicketDTO ticketDTO = ticketMapper.toDto(ticketRepository.findById(1L).orElseThrow());
        ticketDTO.setRequestTitle("Updated Request Title");
        ticketDTO.setCurrentStateId(2L);
        ticketDTO.setSize(TShirtSize.XL);
        ticketDTO.setEstimate(15);

        TicketDTO updatedRequest = ticketService.updateTicket(ticketDTO);

        assertThat(updatedRequest.getRequestTitle()).isEqualTo("Updated Request Title");
        assertThat(updatedRequest.getCurrentStateId()).isEqualTo(2L);
        assertThat(ticketDTO.getSize()).isEqualTo(TShirtSize.XL);
        assertThat(ticketDTO.getEstimate()).isEqualTo(15);
        List<EntityWatcher> watchers =
                entityWatcherRepository.findByEntityTypeAndEntityId(EntityType.Ticket, 1L);
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

        ArgumentCaptor<TicketWorkStateTransitionEvent> workflowStateTransitionEventCaptor =
                ArgumentCaptor.forClass(TicketWorkStateTransitionEvent.class);
        verify(spyEventPublisher, times(1))
                .publishEvent(workflowStateTransitionEventCaptor.capture());

        TicketWorkStateTransitionEvent capturedEvent =
                workflowStateTransitionEventCaptor.getValue();

        assertThat(capturedEvent.getSourceStateId()).isEqualTo(1L);
        assertThat(capturedEvent.getTargetStateId()).isEqualTo(2L);
    }

    @Test
    void shouldFindNextTicketSuccessfully() {
        TicketDTO nextEntity = ticketService.getNextTicket(11L, null).orElseThrow();

        // Then: Validate key properties in a single assertion block
        assertThat(nextEntity)
                .isNotNull()
                .extracting(
                        TicketDTO::getId,
                        TicketDTO::getTeamId,
                        TicketDTO::getWorkflowId,
                        TicketDTO::getRequestTitle,
                        TicketDTO::getRequestDescription,
                        TicketDTO::getChannel,
                        TicketDTO::getIsNew,
                        TicketDTO::getIsCompleted,
                        TicketDTO::getEstimatedCompletionDate,
                        TicketDTO::getSize,
                        TicketDTO::getEstimate)
                .containsExactly(
                        12L,
                        1L,
                        1L, // ID, team, workflow
                        "Refund Status Inquiry",
                        "Customer wants an update on refund.",
                        TicketChannel.CHAT,
                        true,
                        false,
                        LocalDate.of(2025, 2, 17),
                        TShirtSize.S,
                        9);

        assertThat(nextEntity.getCreatedAt()).isNotNull();
        assertThat(nextEntity.getModifiedAt()).isNotNull();
        assertThat(nextEntity.getNumberAttachments()).isGreaterThanOrEqualTo(0);
    }

    @Test
    void shouldFindPreviousTicketSuccessfully() {
        TicketDTO previousEntity = ticketService.getPreviousTicket(11L, null).orElseThrow();

        // Then: Validate key properties in a single assertion block
        assertThat(previousEntity)
                .isNotNull()
                .extracting(
                        TicketDTO::getId,
                        TicketDTO::getTeamId,
                        TicketDTO::getWorkflowId,
                        TicketDTO::getRequestTitle,
                        TicketDTO::getRequestDescription,
                        TicketDTO::getChannel,
                        TicketDTO::getIsNew,
                        TicketDTO::getIsCompleted,
                        TicketDTO::getEstimatedCompletionDate)
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
