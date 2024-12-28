package io.flowinquiry.modules.teams.service.dto;

import io.flowinquiry.modules.teams.domain.TeamRequestPriority;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PriorityDistributionDTO {
    private TeamRequestPriority priority;
    private Long ticketCount;
}
