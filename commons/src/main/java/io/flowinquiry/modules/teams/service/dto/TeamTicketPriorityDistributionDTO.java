package io.flowinquiry.modules.teams.service.dto;

import io.flowinquiry.modules.teams.domain.TeamRequestPriority;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeamTicketPriorityDistributionDTO {
    private Long teamId;
    private String teamName;
    private TeamRequestPriority priority;
    private Long count;
}
