package io.flexwork.modules.teams.service.dto;

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
    private Long ticketCount;

    public TicketActionCountByDateDTO(Date creationInstant, Long ticketCount) {
        // Convert Instant to LocalDate
        this.date = creationInstant.toLocalDate();
        this.ticketCount = ticketCount;
    }
}
