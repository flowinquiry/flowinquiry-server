package io.flowinquiry.modules.teams.service.listener;

import io.flowinquiry.modules.collab.service.MailService;
import io.flowinquiry.modules.teams.repository.TeamRequestWatcherRepository;
import io.flowinquiry.modules.teams.service.event.NewTeamRequestCreatedEvent;
import io.flowinquiry.modules.teams.service.mapper.WatcherMapper;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class TeamRequestCommentCreatedMailEventListener {
    private final WatcherMapper watcherMapper;
    private final TeamRequestWatcherRepository teamRequestWatcherRepository;
    private final MailService mailService;

    public TeamRequestCommentCreatedMailEventListener(
            WatcherMapper watcherMapper,
            TeamRequestWatcherRepository teamRequestWatcherRepository,
            MailService mailService) {
        this.watcherMapper = watcherMapper;
        this.teamRequestWatcherRepository = teamRequestWatcherRepository;
        this.mailService = mailService;
    }

    @Async("asyncTaskExecutor")
    @Transactional
    @EventListener
    public void onTeamRequestCommentCreated(NewTeamRequestCreatedEvent event) {}
}
