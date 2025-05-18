package io.flowinquiry.modules.teams.service.job;

import static io.flowinquiry.modules.teams.utils.DeduplicationKeyBuilder.buildSlaWarningKey;
import static j2html.TagCreator.a;
import static j2html.TagCreator.p;
import static j2html.TagCreator.strong;
import static j2html.TagCreator.text;

import io.flowinquiry.modules.collab.EmailContext;
import io.flowinquiry.modules.collab.domain.Notification;
import io.flowinquiry.modules.collab.domain.NotificationType;
import io.flowinquiry.modules.collab.service.MailService;
import io.flowinquiry.modules.shared.service.cache.DeduplicationCacheService;
import io.flowinquiry.modules.teams.domain.Ticket;
import io.flowinquiry.modules.teams.domain.WorkflowTransitionHistory;
import io.flowinquiry.modules.teams.service.TeamService;
import io.flowinquiry.modules.teams.service.WorkflowTransitionHistoryService;
import io.flowinquiry.modules.usermanagement.domain.User;
import io.flowinquiry.modules.usermanagement.service.mapper.UserMapper;
import io.flowinquiry.utils.Obfuscator;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Profile("!test")
@Component
public class SendNotificationForTicketsViolateSlaJob {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm z");

    private final SimpMessagingTemplate messageTemplate;

    private final TeamService teamService;

    private final WorkflowTransitionHistoryService workflowTransitionHistoryService;

    private final MailService mailService;

    private final DeduplicationCacheService deduplicationCacheService;

    private final UserMapper userMapper;

    public SendNotificationForTicketsViolateSlaJob(
            SimpMessagingTemplate messageTemplate,
            TeamService teamService,
            WorkflowTransitionHistoryService workflowTransitionHistoryService,
            MailService mailService,
            DeduplicationCacheService deduplicationCacheService,
            UserMapper userMapper) {
        this.messageTemplate = messageTemplate;
        this.teamService = teamService;
        this.workflowTransitionHistoryService = workflowTransitionHistoryService;
        this.mailService = mailService;
        this.deduplicationCacheService = deduplicationCacheService;
        this.userMapper = userMapper;
    }

    @Scheduled(cron = "0 0/1 * * * ?")
    @SchedulerLock(name = "SendNotificationForTicketsViolateSlaJob")
    @Transactional
    public void run() {
        List<WorkflowTransitionHistory> violatingTickets =
                workflowTransitionHistoryService.getViolatedTransitions();

        for (WorkflowTransitionHistory violatingTicket : violatingTickets) {
            Ticket ticket = violatingTicket.getTicket();
            Instant slaDueDate = violatingTicket.getSlaDueDate();
            String formattedSlaDueDate = slaDueDate.atZone(ZoneId.of("UTC")).format(formatter);

            // ✅ Escalate status
            workflowTransitionHistoryService.escalateTransition(violatingTicket.getId());

            // ✅ Fetch assign user (if exists)
            User assignUser = ticket.getAssignUser();

            // ✅ Fetch all team managers
            List<User> teamManagers = teamService.getTeamManagers(ticket.getTeam().getId());

            // ✅ Collect all recipients (Assign User + Team Managers)
            Set<User> recipients = new HashSet<>();
            if (assignUser != null) {
                recipients.add(assignUser);
            }
            recipients.addAll(teamManagers); // Always notify managers

            for (User recipient : recipients) {
                // ✅ Build unique cache key per user to prevent duplicate notifications
                String cacheKey =
                        buildSlaWarningKey(
                                recipient.getId(),
                                ticket.getId(),
                                violatingTicket.getTicket().getWorkflow().getId(),
                                violatingTicket.getEventName(),
                                violatingTicket.getToState().getId(),
                                "SendNotificationForTicketsViolateSlaJob");

                if (deduplicationCacheService.containsKey(cacheKey)) {
                    continue;
                }

                // ✅ Create notification content
                String html =
                        p(
                                        text("The ticket "),
                                        a(ticket.getRequestTitle())
                                                .withHref(
                                                        "/portal/teams/"
                                                                + Obfuscator.obfuscate(
                                                                        ticket.getTeam().getId())
                                                                + "/requests/"
                                                                + Obfuscator.obfuscate(
                                                                        ticket.getId())),
                                        text(
                                                " assigned to you or your team has violated its SLA. The SLA was due on "),
                                        strong(text(formattedSlaDueDate)),
                                        text(". Please take necessary action immediately."))
                                .render();

                // ✅ Create notification object
                Notification notification =
                        Notification.builder()
                                .content(html)
                                .type(NotificationType.SLA_BREACH)
                                .user(User.builder().id(recipient.getId()).build())
                                .isRead(false)
                                .build();

                // ✅ Send WebSocket notification
                messageTemplate.convertAndSendToUser(
                        String.valueOf(recipient.getId()), "/queue/notifications", notification);

                EmailContext emailContext =
                        new EmailContext(Locale.forLanguageTag("en"))
                                .setToUser(userMapper.toDto(recipient))
                                .setSubject(
                                        "email.ticket.sla.violation.subject",
                                        ticket.getRequestTitle(),
                                        ticket.getTeam().getName())
                                .addVariable("requestTitle", ticket.getRequestTitle())
                                .addVariable(
                                        "obfuscatedTeamId",
                                        Obfuscator.obfuscate(ticket.getTeam().getId()))
                                .addVariable(
                                        "obfuscatedTicketId", Obfuscator.obfuscate(ticket.getId()))
                                .addVariable("slaDueDate", formattedSlaDueDate)
                                .setTemplate("mail/violatedSlaTicketEmail");

                mailService.sendEmail(emailContext);

                // ✅ Store Key in Deduplication Cache
                deduplicationCacheService.put(cacheKey, Duration.ofHours(24));
            }

            log.debug("SLA violation notification sent for ticket {}", violatingTicket);
        }
    }
}
