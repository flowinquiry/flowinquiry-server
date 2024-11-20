package io.flexwork.modules.teams.service.dto;

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
    private Long requestUserId;
    private String requestUserName;
    private Long assignUserId;
    private String assignUserName;
    private String requestTitle;
    private String requestDescription;
    private LocalDateTime createdDate;
    private String currentState;
}
