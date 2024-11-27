package io.flexwork.modules.teams.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SlaDurationDTO {
    private String sourceState;
    private String targetState;
    private Long slaDuration;
    private String eventName;
}
