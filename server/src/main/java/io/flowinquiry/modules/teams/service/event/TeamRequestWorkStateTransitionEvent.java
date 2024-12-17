package io.flowinquiry.modules.teams.service.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class TeamRequestWorkStateTransitionEvent extends ApplicationEvent {

    private Long teamRequestId;

    private Long sourceStateId;

    private Long targetStateId;

    public TeamRequestWorkStateTransitionEvent(
            Object source, Long teamRequestId, Long sourceStateId, Long targetStateId) {
        super(source);
        this.teamRequestId = teamRequestId;
        this.sourceStateId = sourceStateId;
        this.targetStateId = targetStateId;
    }
}
