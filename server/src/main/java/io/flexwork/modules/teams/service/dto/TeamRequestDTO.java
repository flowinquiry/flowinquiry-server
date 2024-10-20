package io.flexwork.modules.teams.service.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TeamRequestDTO {

    private Long id;
    private Long workflowId;
    private Long currentStatusId;
    private Long createdById;
    private LocalDateTime createdAt;
    private Long updatedById;
    private LocalDateTime updatedAt;
}
