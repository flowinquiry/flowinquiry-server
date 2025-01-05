package io.flowinquiry.modules.teams.service.listener;

import io.flowinquiry.modules.collab.EmailContext;
import io.flowinquiry.modules.collab.service.MailService;
import io.flowinquiry.modules.teams.repository.TeamRequestWatcherRepository;
import io.flowinquiry.modules.teams.service.TeamRequestService;
import io.flowinquiry.modules.teams.service.dto.TeamRequestDTO;
import io.flowinquiry.modules.teams.service.dto.WatcherDTO;
import io.flowinquiry.modules.teams.service.event.NewTeamRequestCreatedEvent;
import io.flowinquiry.modules.teams.service.mapper.WatcherMapper;
import java.util.List;
import java.util.Locale;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class NewTeamRequestCreatedMailEventListener {

    private final WatcherMapper watcherMapper;
    private final TeamRequestWatcherRepository teamRequestWatcherRepository;
    private TeamRequestService teamRequestService;
    private final MailService mailService;

    public NewTeamRequestCreatedMailEventListener(
            TeamRequestWatcherRepository teamRequestWatcherRepository,
            TeamRequestService teamRequestService,
            MailService mailService,
            WatcherMapper watcherMapper) {
        this.teamRequestWatcherRepository = teamRequestWatcherRepository;
        this.teamRequestService = teamRequestService;
        this.mailService = mailService;
        this.watcherMapper = watcherMapper;
    }

    @Async("asyncTaskExecutor")
    @Transactional
    @EventListener
    public void onNewTeamRequestCreated(NewTeamRequestCreatedEvent event) {
        TeamRequestDTO teamRequestDTO =
                teamRequestService.getTeamRequestById(event.getTeamRequest().getId());
        List<WatcherDTO> watchers =
                teamRequestWatcherRepository.findWatchersByRequestId(teamRequestDTO.getId());

        if (!watchers.isEmpty()) {
            watchers.forEach(
                    watcher -> {
                        EmailContext emailContext =
                                new EmailContext(Locale.forLanguageTag("en"))
                                        .setToUser(watcherMapper.toUserDto(watcher))
                                        .setSubject(
                                                "mail.newticket.title",
                                                teamRequestDTO.getRequestTitle())
                                        .addVariable("ticket", teamRequestDTO)
                                        .setTemplate("mail/newTicketEmail");
                        mailService.sendEmail(emailContext);
                    });
        }
    }
}
