package io.flowinquiry.modules.teams.service.listener;

import io.flowinquiry.modules.teams.service.event.NewTeamRequestCreatedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class NewTeamCreatedEventListener {

    @Async("asyncTaskExecutor")
    @Transactional
    @EventListener
    public void onNewTeamCreated(NewTeamRequestCreatedEvent event) {}
}
