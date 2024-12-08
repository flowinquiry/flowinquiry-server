package io.flexwork.modules.teams.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WorkflowTransitionDTO {
    private Long id;
    private Long workflowId;
    private Long sourceStateId;
    private Long targetStateId;
    private String eventName;
    private Long slaDuration;
    private boolean escalateOnViolation;
}
