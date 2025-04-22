package io.flowinquiry.modules.teams.service.listener;

import io.flowinquiry.modules.collab.EmailContext;
import io.flowinquiry.modules.collab.domain.EntityType;
import io.flowinquiry.modules.collab.domain.EntityWatcher;
import io.flowinquiry.modules.collab.repository.EntityWatcherRepository;
import io.flowinquiry.modules.collab.service.MailService;
import io.flowinquiry.modules.teams.service.TeamRequestService;
import io.flowinquiry.modules.teams.service.dto.TeamRequestDTO;
import io.flowinquiry.modules.teams.service.event.NewTeamRequestCreatedEvent;
import io.flowinquiry.modules.usermanagement.service.mapper.UserMapper;
import java.util.List;
import java.util.Locale;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class NewTeamRequestCreatedMailEventListener {

    private final UserMapper userMapper;
    private final EntityWatcherRepository entityWatcherRepository;
    private final TeamRequestService teamRequestService;
    private final MailService mailService;

    public NewTeamRequestCreatedMailEventListener(
            EntityWatcherRepository entityWatcherRepository,
            TeamRequestService teamRequestService,
            MailService mailService,
            UserMapper userMapper) {
        this.entityWatcherRepository = entityWatcherRepository;
        this.teamRequestService = teamRequestService;
        this.mailService = mailService;
        this.userMapper = userMapper;
    }

    @Async("asyncTaskExecutor")
    @Transactional
    @EventListener
    public void onNewTeamRequestCreated(NewTeamRequestCreatedEvent event) {
        TeamRequestDTO teamRequestDTO =
                teamRequestService.getTeamRequestById(event.getTeamRequest().getId());
        List<EntityWatcher> watchers =
                entityWatcherRepository.findByEntityTypeAndEntityId(
                        EntityType.Team_Request, teamRequestDTO.getId());

        if (!watchers.isEmpty()) {
            watchers.forEach(
                    watcher -> {
                        EmailContext emailContext =
                                new EmailContext(Locale.forLanguageTag("en"))
                                        .setToUser(userMapper.toDto(watcher.getWatchUser()))
                                        .setSubject(
                                                "email.new.ticket.subject",
                                                teamRequestDTO.getRequestTitle())
                                        .addVariable("ticket", teamRequestDTO)
                                        .setTemplate("mail/newTicketEmail");
                        mailService.sendEmail(emailContext);
                    });
        }
    }
}
