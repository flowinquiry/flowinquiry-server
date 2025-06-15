package io.flowinquiry.modules.teams.service.listener;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.flowinquiry.modules.collab.domain.EntityType;
import io.flowinquiry.modules.collab.service.dto.CommentDTO;
import io.flowinquiry.modules.teams.service.TicketHealthEvalService;
import io.flowinquiry.modules.teams.service.TicketService;
import io.flowinquiry.modules.teams.service.dto.TicketDTO;
import io.flowinquiry.modules.teams.service.event.TicketCommentCreatedEvent;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TicketCommentCreatedAiEvaluateConversationHealthEventListenerTest {

    @Mock private TicketService ticketService;

    @Mock private TicketHealthEvalService ticketHealthEvalService;

    private TicketCommentCreatedAiEvaluateConversationHealthEventListener listener;

    @BeforeEach
    public void setup() {
        listener =
                new TicketCommentCreatedAiEvaluateConversationHealthEventListener(
                        ticketService, ticketHealthEvalService);
    }

    @Test
    public void testOnTicketNewCommentAiEvaluateConversationHealthEvent_RequestUserIsCommenter() {
        // Given
        Long ticketId = 1L;
        Long userId = 101L;
        String commentContent = "This is a test comment";

        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setId(1L);
        commentDTO.setContent(commentContent);
        commentDTO.setCreatedById(userId);
        commentDTO.setCreatedByName("John Doe");
        commentDTO.setCreatedAt(Instant.now());
        commentDTO.setEntityType(EntityType.Ticket);
        commentDTO.setEntityId(ticketId);

        TicketDTO ticketDTO =
                TicketDTO.builder()
                        .id(ticketId)
                        .requestUserId(userId) // Same as commenter
                        .build();

        TicketCommentCreatedEvent event = new TicketCommentCreatedEvent(this, commentDTO);

        when(ticketService.getTicketById(ticketId)).thenReturn(ticketDTO);

        // When
        listener.onTicketNewCommentAiEvaluateConversationHealthEvent(event);

        // Then
        verify(ticketService).getTicketById(ticketId);
        verify(ticketHealthEvalService)
                .evaluateConversationHealth(eq(ticketId), eq(commentContent), eq(true));
    }

    @Test
    public void
            testOnTicketNewCommentAiEvaluateConversationHealthEvent_RequestUserIsNotCommenter() {
        // Given
        Long ticketId = 1L;
        Long commenterId = 101L;
        Long requestUserId = 102L; // Different from commenter
        String commentContent = "This is a test comment";

        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setId(1L);
        commentDTO.setContent(commentContent);
        commentDTO.setCreatedById(commenterId);
        commentDTO.setCreatedByName("John Doe");
        commentDTO.setCreatedAt(Instant.now());
        commentDTO.setEntityType(EntityType.Ticket);
        commentDTO.setEntityId(ticketId);

        TicketDTO ticketDTO = TicketDTO.builder().id(ticketId).requestUserId(requestUserId).build();

        TicketCommentCreatedEvent event = new TicketCommentCreatedEvent(this, commentDTO);

        when(ticketService.getTicketById(ticketId)).thenReturn(ticketDTO);

        // When
        listener.onTicketNewCommentAiEvaluateConversationHealthEvent(event);

        // Then
        verify(ticketService).getTicketById(ticketId);
        verify(ticketHealthEvalService)
                .evaluateConversationHealth(eq(ticketId), eq(commentContent), eq(false));
    }
}
