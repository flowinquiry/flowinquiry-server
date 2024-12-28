package io.flowinquiry.modules.teams.service.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class RemoveUserOutOfTeamEvent extends ApplicationEvent {
    private final Long teamId;

    private final Long userId;

    public RemoveUserOutOfTeamEvent(Object source, Long teamId, Long userId) {
        super(source);
        this.teamId = teamId;
        this.userId = userId;
    }
}
