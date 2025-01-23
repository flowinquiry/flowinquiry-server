package io.flowinquiry.modules.teams.service.dto;

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
}
