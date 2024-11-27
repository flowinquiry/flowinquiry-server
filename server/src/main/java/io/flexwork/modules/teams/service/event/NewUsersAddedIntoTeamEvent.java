package io.flexwork.modules.teams.service.event;

import java.util.List;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class NewUsersAddedIntoTeamEvent extends ApplicationEvent {

    private List<Long> userIds;

    private Long teamId;

    private String roleName;

    public NewUsersAddedIntoTeamEvent(
            Object source, List<Long> userIds, Long teamId, String roleName) {
        super(source);
        this.userIds = userIds;
        this.teamId = teamId;
        this.roleName = roleName;
    }
}
