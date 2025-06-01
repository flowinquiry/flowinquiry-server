package io.flowinquiry.modules.teams.service.listener;

import static io.flowinquiry.modules.teams.utils.TicketPathUtils.buildTicketPath;

import io.flowinquiry.modules.collab.EmailContext;
import io.flowinquiry.modules.collab.domain.EntityType;
import io.flowinquiry.modules.collab.domain.EntityWatcher;
import io.flowinquiry.modules.collab.repository.EntityWatcherRepository;
import io.flowinquiry.modules.collab.service.CommentService;
import io.flowinquiry.modules.collab.service.MailService;
import io.flowinquiry.modules.collab.service.dto.CommentDTO;
import io.flowinquiry.modules.teams.service.TicketService;
import io.flowinquiry.modules.teams.service.dto.TicketDTO;
import io.flowinquiry.modules.teams.service.event.TicketCommentCreatedEvent;
import io.flowinquiry.modules.usermanagement.service.mapper.UserMapper;
import java.util.List;
import java.util.Locale;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class TicketCommentCreatedMailEventListener {
    private final CommentService commentService;
    private final UserMapper userMapper;
    private final EntityWatcherRepository entityWatcherRepository;
    private final TicketService ticketService;
    private final MailService mailService;

    public TicketCommentCreatedMailEventListener(
            CommentService commentService,
            UserMapper userMapper,
            EntityWatcherRepository entityWatcherRepository,
            TicketService ticketService,
            MailService mailService) {
        this.commentService = commentService;
        this.userMapper = userMapper;
        this.entityWatcherRepository = entityWatcherRepository;
        this.ticketService = ticketService;
        this.mailService = mailService;
    }

    @Async("asyncTaskExecutor")
    @Transactional
    @EventListener
    public void onTicketCommentCreated(TicketCommentCreatedEvent event) {
        CommentDTO commentDTO = commentService.getCommentById(event.getCommentDTO().getId());
        List<EntityWatcher> watchers =
                entityWatcherRepository.findByEntityTypeAndEntityId(
                        EntityType.Ticket, commentDTO.getEntityId());

        TicketDTO ticketDTO = ticketService.getTicketById(commentDTO.getEntityId());

        if (!watchers.isEmpty()) {
            for (EntityWatcher watcher : watchers) {
                // Skip sending email if the watcher is the comment creator
                if (watcher.getId().equals(commentDTO.getCreatedById())) {
                    continue;
                }

                String ticketPath = buildTicketPath(ticketDTO);

                EmailContext emailContext =
                        new EmailContext(Locale.forLanguageTag("en"))
                                .setToUser(userMapper.toDto(watcher.getWatchUser()))
                                .setSubject(
                                        "email.new.ticket.comment.subject",
                                        ticketDTO.getRequestTitle())
                                .addVariable("ticket", ticketDTO)
                                .addVariable("comment", commentDTO)
                                .addVariable("ticketPath", ticketPath)
                                .setTemplate("mail/newTicketCommentEmail");

                mailService.sendEmail(emailContext);
            }
        }
    }
}
