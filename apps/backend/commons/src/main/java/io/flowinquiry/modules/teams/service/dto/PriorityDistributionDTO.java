package io.flowinquiry.modules.teams.service.dto;

import io.flowinquiry.modules.teams.domain.TicketPriority;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PriorityDistributionDTO {
    private TicketPriority priority;
    private Long ticketCount;
}
