package io.flowinquiry.modules.teams.service.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class TicketWorkStateTransitionEvent extends ApplicationEvent {

    private final Long ticketId;

    private final Long sourceStateId;

    private final Long targetStateId;

    public TicketWorkStateTransitionEvent(
            Object source, Long ticketId, Long sourceStateId, Long targetStateId) {
        super(source);
        this.ticketId = ticketId;
        this.sourceStateId = sourceStateId;
        this.targetStateId = targetStateId;
    }
}
