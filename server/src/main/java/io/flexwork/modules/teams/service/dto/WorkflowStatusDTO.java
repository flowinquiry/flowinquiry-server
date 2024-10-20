package io.flexwork.modules.teams.service.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WorkflowStatusDTO {

    private Long id;
    private String name;
    private String description;
    private Long workflowId;
    private Integer orderInWorkflow;
    private String statusPhase;
    private Long createdById;
    private LocalDateTime createdAt;
    private Long updatedById;
    private LocalDateTime updatedAt;
}
