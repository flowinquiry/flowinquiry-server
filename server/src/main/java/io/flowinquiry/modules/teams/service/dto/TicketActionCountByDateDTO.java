package io.flowinquiry.modules.teams.service.dto;

import java.sql.Date;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketActionCountByDateDTO {
    private LocalDate date;
    private Long createdCount;
    private Long closedCount;

    public TicketActionCountByDateDTO(Date creationInstant, Long createdCount, Long closedCount) {
        // Convert Instant to LocalDate
        this.date = creationInstant.toLocalDate();
        this.createdCount = createdCount;
        this.closedCount = closedCount;
    }
}
