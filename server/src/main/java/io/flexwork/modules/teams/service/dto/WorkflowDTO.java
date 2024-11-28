package io.flexwork.modules.teams.service.dto;

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

    private String requestName;

    private String description;

    boolean isGlobal;

    private Integer level1EscalationTimeout;

    private Integer level2EscalationTimeout;

    private Integer level3EscalationTimeout;
}
