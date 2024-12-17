package io.flowinquiry.modules.teams.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class WorkflowStateDTO {
    private Long id;
    private Long workflowId;
    private String stateName;
    private Boolean isInitial;
    private Boolean isFinal;
}
