package io.flowinquiry.modules.teams.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class TeamRequestConversationHealthDTO {
    private Long id;
    private Long teamRequestId;
    private Float conversationHealth;
    private Float cumulativeSentiment;
    private Integer totalMessages;
    private Integer totalQuestions;
    private Integer resolvedQuestions;

    /**
     * Calculates the ticket health level based on conversation health, cumulative sentiment, and
     * clarity ratio.
     *
     * @return The ticket health level (EXCELLENT, GOOD, FAIR, POOR, CRITICAL).
     */
    @JsonProperty("healthLevel")
    public TicketHealthLevel getHealthLevel() {
        if (conversationHealth > 0.9) {
            return TicketHealthLevel.EXCELLENT;
        } else if (conversationHealth > 0.8) {
            return TicketHealthLevel.GOOD;
        } else if (conversationHealth > 0.6) {
            return TicketHealthLevel.FAIR;
        } else if (conversationHealth > 0.4) {
            return TicketHealthLevel.POOR;
        } else {
            return TicketHealthLevel.CRITICAL;
        }
    }
}
