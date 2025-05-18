package io.flowinquiry.modules.teams.service.event;

import io.flowinquiry.modules.teams.service.dto.TicketDTO;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class NewTicketCreatedEvent extends ApplicationEvent {
    private final TicketDTO teamRequest;

    public NewTicketCreatedEvent(Object source, TicketDTO teamRequest) {
        super(source);
        this.teamRequest = teamRequest;
    }
}
