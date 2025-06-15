package io.flowinquiry.modules.teams.service.mapper;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.flowinquiry.modules.teams.domain.Ticket;
import io.flowinquiry.modules.teams.domain.TicketConversationHealth;
import io.flowinquiry.modules.teams.service.dto.TicketConversationHealthDTO;
import io.flowinquiry.modules.teams.service.dto.TicketHealthLevel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

public class TicketConversationHealthMapperTest {

    private TicketConversationHealthMapper mapper;

    @BeforeEach
    public void setup() {
        mapper = Mappers.getMapper(TicketConversationHealthMapper.class);
    }

    @Test
    public void testToDTO() {
        // Given
        Ticket ticket = Ticket.builder().id(1L).build();

        TicketConversationHealth entity =
                TicketConversationHealth.builder()
                        .id(2L)
                        .ticket(ticket)
                        .summary("Test summary")
                        .conversationHealth(0.85f)
                        .cumulativeSentiment(0.5f)
                        .totalMessages(10)
                        .totalQuestions(5)
                        .resolvedQuestions(3)
                        .build();

        // When
        TicketConversationHealthDTO dto = mapper.toDTO(entity);

        // Then
        assertNotNull(dto);
        assertAll(
                () -> assertEquals(entity.getId(), dto.getId()),
                () -> assertEquals(entity.getTicket().getId(), dto.getTicketId()),
                () -> assertEquals(entity.getConversationHealth(), dto.getConversationHealth()),
                () -> assertEquals(entity.getCumulativeSentiment(), dto.getCumulativeSentiment()),
                () -> assertEquals(entity.getTotalMessages(), dto.getTotalMessages()),
                () -> assertEquals(entity.getTotalQuestions(), dto.getTotalQuestions()),
                () -> assertEquals(entity.getResolvedQuestions(), dto.getResolvedQuestions()),
                () -> assertEquals(TicketHealthLevel.EXCELLENT, dto.getHealthLevel()));
    }

    @Test
    public void testToEntity() {
        // Given
        TicketConversationHealthDTO dto =
                TicketConversationHealthDTO.builder()
                        .id(2L)
                        .ticketId(1L)
                        .conversationHealth(0.55f)
                        .cumulativeSentiment(0.3f)
                        .totalMessages(8)
                        .totalQuestions(4)
                        .resolvedQuestions(2)
                        .build();

        // When
        TicketConversationHealth entity = mapper.toEntity(dto);

        // Then
        assertNotNull(entity);
        assertAll(
                () -> assertEquals(dto.getId(), entity.getId()),
                () -> assertEquals(dto.getTicketId(), entity.getTicket().getId()),
                () -> assertEquals(dto.getConversationHealth(), entity.getConversationHealth()),
                () -> assertEquals(dto.getCumulativeSentiment(), entity.getCumulativeSentiment()),
                () -> assertEquals(dto.getTotalMessages(), entity.getTotalMessages()),
                () -> assertEquals(dto.getTotalQuestions(), entity.getTotalQuestions()),
                () -> assertEquals(dto.getResolvedQuestions(), entity.getResolvedQuestions()));
    }

    @Test
    public void testHealthLevelCalculation() {
        // Test different health levels based on conversation health values

        // EXCELLENT (>= 0.8)
        testHealthLevelForConversationHealth(0.8f, TicketHealthLevel.EXCELLENT);
        testHealthLevelForConversationHealth(0.9f, TicketHealthLevel.EXCELLENT);

        // GOOD (> 0.6 and < 0.8)
        testHealthLevelForConversationHealth(0.7f, TicketHealthLevel.GOOD);
        testHealthLevelForConversationHealth(0.61f, TicketHealthLevel.GOOD);

        // FAIR (> 0.4 and <= 0.6)
        testHealthLevelForConversationHealth(0.5f, TicketHealthLevel.FAIR);
        testHealthLevelForConversationHealth(0.41f, TicketHealthLevel.FAIR);

        // POOR (> 0.2 and <= 0.4)
        testHealthLevelForConversationHealth(0.3f, TicketHealthLevel.POOR);
        testHealthLevelForConversationHealth(0.21f, TicketHealthLevel.POOR);

        // CRITICAL (<= 0.2)
        testHealthLevelForConversationHealth(0.19f, TicketHealthLevel.CRITICAL);
        testHealthLevelForConversationHealth(0.1f, TicketHealthLevel.CRITICAL);
        testHealthLevelForConversationHealth(0.0f, TicketHealthLevel.CRITICAL);
    }

    private void testHealthLevelForConversationHealth(
            float conversationHealth, TicketHealthLevel expectedLevel) {
        // Given
        Ticket ticket = Ticket.builder().id(1L).build();

        TicketConversationHealth entity =
                TicketConversationHealth.builder()
                        .id(2L)
                        .ticket(ticket)
                        .conversationHealth(conversationHealth)
                        .build();

        // When
        TicketConversationHealthDTO dto = mapper.toDTO(entity);

        // Then
        assertEquals(expectedLevel, dto.getHealthLevel());
    }
}
