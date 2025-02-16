package io.flowinquiry.modules.teams.service.dto;

import java.time.Instant;
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
    private Instant transitionDate;
    private Instant slaDueDate;
    private String status;
}
