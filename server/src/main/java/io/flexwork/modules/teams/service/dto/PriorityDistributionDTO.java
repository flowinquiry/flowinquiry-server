package io.flexwork.modules.teams.service.dto;

import io.flexwork.modules.teams.domain.TeamRequestPriority;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PriorityDistributionDTO {
    private TeamRequestPriority priority;
    private Long ticketCount;
}
