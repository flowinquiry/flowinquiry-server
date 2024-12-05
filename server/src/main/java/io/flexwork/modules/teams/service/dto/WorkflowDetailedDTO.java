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

    private String
            ownerName; // return the team name that own this workflow, if workflow is global then
    // its value is null

    private List<WorkflowStateDTO> states;

    private List<WorkflowTransitionDTO> transitions;
}
