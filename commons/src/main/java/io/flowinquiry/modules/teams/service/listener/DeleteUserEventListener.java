package io.flowinquiry.modules.teams.service.listener;

import io.flowinquiry.modules.teams.service.TeamService;
import io.flowinquiry.modules.teams.service.dto.TeamDTO;
import io.flowinquiry.modules.usermanagement.service.event.DeleteUserEvent;
import java.util.List;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DeleteUserEventListener {

    private final TeamService teamService;

    public DeleteUserEventListener(TeamService teamService) {
        this.teamService = teamService;
    }

    @Async("auditLogExecutor")
    @Transactional
    @EventListener
    public void onDeleteUserEvent(DeleteUserEvent event) {
        List<TeamDTO> teams = teamService.findAllTeamsByUserId(event.getUserId());
        for (TeamDTO team : teams) {
            teamService.removeUserFromTeam(event.getUserId(), team.getId());
        }
    }
}
