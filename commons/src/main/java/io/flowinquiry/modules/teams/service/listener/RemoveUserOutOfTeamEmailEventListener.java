package io.flowinquiry.modules.teams.service.listener;

import io.flowinquiry.modules.teams.service.event.RemoveUserOutOfTeamEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class RemoveUserOutOfTeamEmailEventListener {

    @Async("asyncTaskExecutor")
    @EventListener
    @Transactional
    public void onRemoveUserOutOfTeam(RemoveUserOutOfTeamEvent event) {}
}
