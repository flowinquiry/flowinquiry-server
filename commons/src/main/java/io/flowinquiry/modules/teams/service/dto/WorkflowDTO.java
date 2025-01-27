package io.flowinquiry.modules.teams.service.dto;

import io.flowinquiry.modules.teams.domain.WorkflowVisibility;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class WorkflowDTO {
    private Long id;
    private String name;
    private String description;
    private String requestName;
    private Long ownerId; // ID of the owning team; null for global workflows
    private String ownerName;
    private WorkflowVisibility visibility;
    private Integer level1EscalationTimeout;
    private Integer level2EscalationTimeout;
    private Integer level3EscalationTimeout;
    private String tags;
}
