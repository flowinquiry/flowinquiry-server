package io.flowinquiry.modules.teams.service.dto;

import static io.flowinquiry.modules.teams.service.dto.TicketHealthLevel.CRITICAL;
import static io.flowinquiry.modules.teams.service.dto.TicketHealthLevel.EXCELLENT;
import static io.flowinquiry.modules.teams.service.dto.TicketHealthLevel.FAIR;
import static io.flowinquiry.modules.teams.service.dto.TicketHealthLevel.GOOD;
import static io.flowinquiry.modules.teams.service.dto.TicketHealthLevel.POOR;

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
            return EXCELLENT; // Default to EXCELLENT if data is missing
        }

        // Calculate clarity ratio
        float clarityRatio =
                totalQuestions != null && totalQuestions > 0
                        ? (float) resolvedQuestions / totalQuestions
                        : 0;

        // Determine health level based on thresholds
        if (conversationHealth > 0.9 && clarityRatio > 0.9 && cumulativeSentiment > 0.9) {
            return EXCELLENT;
        } else if (conversationHealth > 0.8 && clarityRatio > 0.8 && cumulativeSentiment > 0.8) {
            return GOOD;
        } else if (conversationHealth > 0.6 && clarityRatio > 0.6 && cumulativeSentiment > 0.6) {
            return FAIR;
        } else if (conversationHealth > 0.4 && clarityRatio > 0.4 && cumulativeSentiment > 0.4) {
            return POOR;
        } else {
            return CRITICAL;
        }
    }
}
