package io.flowinquiry.modules.teams.service.listener;

import static org.mockito.Mockito.verify;

import io.flowinquiry.modules.teams.service.TicketHealthEvalService;
import io.flowinquiry.modules.teams.service.dto.TicketDTO;
import io.flowinquiry.modules.teams.service.event.NewTicketCreatedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class NewTicketCreatedAiSummaryEventListenerTest {

    @Mock private TicketHealthEvalService ticketHealthEvalService;

    private NewTicketCreatedAiSummaryEventListener listener;

    @BeforeEach
    public void setup() {
        listener = new NewTicketCreatedAiSummaryEventListener(ticketHealthEvalService);
    }

    @Test
    public void testOnNewTicketCreated() {
        // Given
        Long ticketId = 1L;
        String title = "Test Ticket";
        String description = "This is a test ticket description";

        TicketDTO ticketDTO =
                TicketDTO.builder()
                        .id(ticketId)
                        .requestTitle(title)
                        .requestDescription(description)
                        .build();

        NewTicketCreatedEvent event = new NewTicketCreatedEvent(this, ticketDTO);

        // When
        listener.onNewTicketCreated(event);

        // Then
        String expectedContent = "Title: " + title + "\n" + "Description: " + description + "\n";
        verify(ticketHealthEvalService).evaluateConversationHealth(ticketId, expectedContent, true);
    }
}
