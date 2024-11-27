package io.flexwork.modules.teams.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowStateDTO {
    private Long id;
    private Long workflowId;
    private String stateName;
    private Boolean isInitial;
    private Boolean isFinal;
}
