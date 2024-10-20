package io.flexwork.modules.teams.service.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TeamRequestStatusDTO {
    private Long id;
    private Long teamRequestId;
    private Long statusId;
    private Long updatedById;
    private LocalDateTime updatedAt;
    private String comments;
}
