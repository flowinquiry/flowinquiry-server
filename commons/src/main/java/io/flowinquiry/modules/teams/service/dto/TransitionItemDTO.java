package io.flowinquiry.modules.teams.service.dto;

import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransitionItemDTO {
    private String fromState;
    private String toState;
    private String eventName;
    private ZonedDateTime transitionDate;
    private ZonedDateTime slaDueDate;
    private String status;
}
