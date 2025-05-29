package io.flowinquiry.modules.teams.service.event;

import io.flowinquiry.modules.teams.domain.WorkflowTransitionHistory;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class TicketViolateSlaEvent extends ApplicationEvent {

    private final WorkflowTransitionHistory violatingTicket;

    public TicketViolateSlaEvent(Object source, WorkflowTransitionHistory violatingTicket) {
        super(source);
        this.violatingTicket = violatingTicket;
    }
}
