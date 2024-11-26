package io.flexwork.modules.teams.service.dto;

import io.flexwork.modules.teams.domain.TicketChannel;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeamRequestDTO {
    private Long id;
    private Long teamId;
    private String teamName;
    private Long workflowId;
    private String workflowName;
    private String workflowRequestName;
    private Long requestUserId;
    private String requestUserName;
    private String requestUserImageUrl;
    private Long assignUserId;
    private String assignUserName;
    private String assignUserImageUrl;
    private String requestTitle;
    private String requestDescription;
    private String priority;
    private LocalDateTime createdDate;
    private LocalDate estimatedCompletionDate;
    private LocalDate actualCompletionDate;
    private String currentState;
    private TicketChannel channel;
}
