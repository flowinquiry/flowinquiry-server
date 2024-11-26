package io.flexwork.modules.teams.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowDTO {

    private Long id;

    private String name;

    private String requestName;

    private String description;

    boolean isGlobal;
}
