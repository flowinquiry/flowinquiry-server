package io.flowinquiry.modules.teams.service.listener;

import io.flowinquiry.modules.collab.EmailContext;
import io.flowinquiry.modules.collab.service.CommentService;
import io.flowinquiry.modules.collab.service.MailService;
import io.flowinquiry.modules.collab.service.dto.CommentDTO;
import io.flowinquiry.modules.teams.repository.TeamRequestWatcherRepository;
import io.flowinquiry.modules.teams.service.TeamRequestService;
import io.flowinquiry.modules.teams.service.dto.TeamRequestDTO;
import io.flowinquiry.modules.teams.service.dto.WatcherDTO;
import io.flowinquiry.modules.teams.service.event.TeamRequestCommentCreatedEvent;
import io.flowinquiry.modules.teams.service.mapper.WatcherMapper;
import io.flowinquiry.utils.Obfuscator;
import java.util.List;
import java.util.Locale;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class TeamRequestCommentCreatedMailEventListener {
    private final CommentService commentService;
    private final WatcherMapper watcherMapper;
    private final TeamRequestWatcherRepository teamRequestWatcherRepository;
    private final TeamRequestService teamRequestService;
    private final MailService mailService;

    public TeamRequestCommentCreatedMailEventListener(
            CommentService commentService,
            WatcherMapper watcherMapper,
            TeamRequestWatcherRepository teamRequestWatcherRepository,
            TeamRequestService teamRequestService,
            MailService mailService) {
        this.commentService = commentService;
        this.watcherMapper = watcherMapper;
        this.teamRequestWatcherRepository = teamRequestWatcherRepository;
        this.teamRequestService = teamRequestService;
        this.mailService = mailService;
    }

    @Async("asyncTaskExecutor")
    @Transactional
    @EventListener
    public void onTeamRequestCommentCreated(TeamRequestCommentCreatedEvent event) {
        CommentDTO commentDTO = commentService.getCommentById(event.getCommentDTO().getId());
        List<WatcherDTO> watchers =
                teamRequestWatcherRepository.findWatchersByRequestId(commentDTO.getEntityId());

        TeamRequestDTO teamRequestDTO =
                teamRequestService.getTeamRequestById(commentDTO.getEntityId());

        if (!watchers.isEmpty()) {
            for (WatcherDTO watcher : watchers) {
                // Skip sending email if the watcher is the comment creator
                if (watcher.getId().equals(commentDTO.getCreatedById())) {
                    continue;
                }

                EmailContext emailContext =
                        new EmailContext(Locale.forLanguageTag("en"))
                                .setToUser(watcherMapper.toUserDto(watcher))
                                .setSubject(
                                        "email.new.ticket.comment.subject",
                                        teamRequestDTO.getRequestTitle())
                                .addVariable("ticket", teamRequestDTO)
                                .addVariable("comment", commentDTO)
                                .addVariable(
                                        "obfuscatedTeamId",
                                        Obfuscator.obfuscate(teamRequestDTO.getTeamId()))
                                .addVariable(
                                        "obfuscatedTicketId",
                                        Obfuscator.obfuscate(teamRequestDTO.getId()))
                                .setTemplate("mail/newTicketCommentEmail");

                mailService.sendEmail(emailContext);
            }
        }
    }
}
