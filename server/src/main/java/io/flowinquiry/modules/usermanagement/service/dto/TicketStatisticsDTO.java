package io.flowinquiry.modules.usermanagement.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketStatisticsDTO {
    private Long totalTickets;
    private Long pendingTickets;
    private Long completedTickets;
}
