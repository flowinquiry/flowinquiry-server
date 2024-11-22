package io.flexwork.modules.collab.service.event;

import io.flexwork.modules.teams.service.dto.TeamRequestDTO;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class NewTeamRequestCreatedEvent extends ApplicationEvent {
    private TeamRequestDTO teamRequest;

    public NewTeamRequestCreatedEvent(Object source, TeamRequestDTO teamRequest) {
        super(source);
        this.teamRequest = teamRequest;
    }
}
