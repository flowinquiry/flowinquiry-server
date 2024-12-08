package io.flexwork.modules.teams.service.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class WorkflowDetailedDTO extends WorkflowDTO {
    private List<WorkflowStateDTO> states;
    private List<WorkflowTransitionDTO> transitions;
}
