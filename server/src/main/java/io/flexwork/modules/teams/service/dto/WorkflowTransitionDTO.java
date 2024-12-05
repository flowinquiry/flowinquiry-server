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
    private Long workflowId; // ID of the associated Workflow
    private Long sourceStateId; // ID of the source state
    private Long targetStateId; // ID of the target state
    private String eventName; // Name of the triggering event
    private Long slaDuration; // SLA duration for the transition (nullable)
    private boolean escalateOnViolation; // Whether to escalate on SLA violation
}
