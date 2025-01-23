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
        if (conversationHealth == null || cumulativeSentiment == null) {
            return TicketHealthLevel.CRITICAL; // Default to CRITICAL if data is missing
        }

        // Calculate clarity ratio
        float clarityRatio =
                totalQuestions != null && totalQuestions > 0
                        ? (float) resolvedQuestions / totalQuestions
                        : 0;

        // Determine health level based on thresholds
        if (conversationHealth > 0.9 && clarityRatio > 0.9 && cumulativeSentiment > 0.9) {
            return TicketHealthLevel.EXCELLENT;
        } else if (conversationHealth > 0.8 && clarityRatio > 0.8 && cumulativeSentiment > 0.8) {
            return TicketHealthLevel.GOOD;
        } else if (conversationHealth > 0.6 && clarityRatio > 0.6 && cumulativeSentiment > 0.6) {
            return TicketHealthLevel.FAIR;
        } else if (conversationHealth > 0.4 && clarityRatio > 0.4 && cumulativeSentiment > 0.4) {
            return TicketHealthLevel.POOR;
        } else {
            return TicketHealthLevel.CRITICAL;
        }
    }
}
