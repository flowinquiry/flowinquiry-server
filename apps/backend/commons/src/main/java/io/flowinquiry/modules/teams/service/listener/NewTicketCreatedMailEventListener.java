package io.flowinquiry.modules.teams.service.listener;

import static io.flowinquiry.modules.teams.utils.TicketPathUtils.buildTicketPath;

import io.flowinquiry.modules.collab.EmailContext;
import io.flowinquiry.modules.collab.domain.EntityType;
import io.flowinquiry.modules.collab.domain.EntityWatcher;
import io.flowinquiry.modules.collab.repository.EntityWatcherRepository;
import io.flowinquiry.modules.collab.service.MailService;
import io.flowinquiry.modules.teams.service.TicketService;
import io.flowinquiry.modules.teams.service.dto.TicketDTO;
import io.flowinquiry.modules.teams.service.event.NewTicketCreatedEvent;
import io.flowinquiry.modules.usermanagement.service.mapper.UserMapper;
import java.util.List;
import java.util.Locale;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class NewTicketCreatedMailEventListener {

    private final UserMapper userMapper;
    private final EntityWatcherRepository entityWatcherRepository;
    private final TicketService ticketService;
    private final MailService mailService;

    public NewTicketCreatedMailEventListener(
            EntityWatcherRepository entityWatcherRepository,
            TicketService ticketService,
            MailService mailService,
            UserMapper userMapper) {
        this.entityWatcherRepository = entityWatcherRepository;
        this.ticketService = ticketService;
        this.mailService = mailService;
        this.userMapper = userMapper;
    }

    @Async("asyncTaskExecutor")
    @Transactional
    @EventListener
    public void onNewTicketCreated(NewTicketCreatedEvent event) {
        TicketDTO ticketDTO = ticketService.getTicketById(event.getTicket().getId());
        List<EntityWatcher> watchers =
                entityWatcherRepository.findByEntityTypeAndEntityId(
                        EntityType.Ticket, ticketDTO.getId());

        if (!watchers.isEmpty()) {
            watchers.forEach(
                    watcher -> {
                        String ticketPath = buildTicketPath(ticketDTO);
                        EmailContext emailContext =
                                new EmailContext(Locale.forLanguageTag("en"))
                                        .setToUser(userMapper.toDto(watcher.getWatchUser()))
                                        .setSubject(
                                                "email.new.ticket.subject",
                                                ticketDTO.getRequestTitle())
                                        .addVariable("ticket", ticketDTO)
                                        .addVariable("ticketPath", ticketPath)
                                        .setTemplate("mail/newTicketEmail");
                        mailService.sendEmail(emailContext);
                    });
        }
    }
}
