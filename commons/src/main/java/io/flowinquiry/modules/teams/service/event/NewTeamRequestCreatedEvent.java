package io.flowinquiry.modules.teams.service.event;

import io.flowinquiry.modules.teams.service.dto.TeamRequestDTO;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class NewTeamRequestCreatedEvent extends ApplicationEvent {
    private final TeamRequestDTO teamRequest;

    public NewTeamRequestCreatedEvent(Object source, TeamRequestDTO teamRequest) {
        super(source);
        this.teamRequest = teamRequest;
    }
}
