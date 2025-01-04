package io.flowinquiry.modules.teams.service.listener;

import io.flowinquiry.modules.teams.service.event.NewUsersAddedIntoTeamEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class NewUserAddedIntoTeamEmailEventListener {

    @Async("auditLogExecutor")
    @EventListener
    @Transactional
    public void onNewUsersAddedIntoTeam(NewUsersAddedIntoTeamEvent event) {}
}
