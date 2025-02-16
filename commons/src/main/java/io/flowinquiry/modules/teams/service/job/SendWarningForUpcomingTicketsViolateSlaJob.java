package io.flowinquiry.modules.teams.service.job;

import static io.flowinquiry.modules.teams.utils.DeduplicationKeyBuilder.buildSlaWarningKey;
import static j2html.TagCreator.a;
import static j2html.TagCreator.p;
import static j2html.TagCreator.strong;
import static j2html.TagCreator.text;

import io.flowinquiry.modules.collab.domain.Notification;
import io.flowinquiry.modules.collab.domain.NotificationType;
import io.flowinquiry.modules.shared.service.cache.DeduplicationCacheService;
import io.flowinquiry.modules.teams.domain.TeamRequest;
import io.flowinquiry.modules.teams.domain.WorkflowTransitionHistory;
import io.flowinquiry.modules.teams.service.TeamService;
import io.flowinquiry.modules.teams.service.WorkflowTransitionHistoryService;
import io.flowinquiry.modules.usermanagement.domain.User;
import io.flowinquiry.utils.Obfuscator;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Profile("!test")
@Component
public class SendWarningForUpcomingTicketsViolateSlaJob {

    // send the warning to people before 30 minutes the SLAs are violated, this value should get
    // from team settings
    private static final int PRIOR_SLA_WARNING_THRESHOLD_IN_SECONDS = 1800;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm z");

    private final SimpMessagingTemplate messageTemplate;

    private final TeamService teamService;

    private final WorkflowTransitionHistoryService workflowTransitionHistoryService;

    private final DeduplicationCacheService deduplicationCacheService;

    public SendWarningForUpcomingTicketsViolateSlaJob(
            SimpMessagingTemplate messageTemplate,
            TeamService teamService,
            WorkflowTransitionHistoryService workflowTransitionHistoryService,
            DeduplicationCacheService deduplicationCacheService) {
        this.messageTemplate = messageTemplate;
        this.teamService = teamService;
        this.workflowTransitionHistoryService = workflowTransitionHistoryService;
        this.deduplicationCacheService = deduplicationCacheService;
    }

    @Scheduled(cron = "0 0/15 * * * ?")
    @SchedulerLock(name = "SendWarningForUpcomingTicketsViolateSlaJob")
    public void run() {
        List<WorkflowTransitionHistory> violatingTickets =
                workflowTransitionHistoryService.getViolatingTransitions(
                        PRIOR_SLA_WARNING_THRESHOLD_IN_SECONDS);

        for (WorkflowTransitionHistory violatingTicket : violatingTickets) {
            TeamRequest teamRequest = violatingTicket.getTeamRequest();
            Instant slaDueDate = violatingTicket.getSlaDueDate();
            String formattedSlaDueDate = slaDueDate.atZone(ZoneId.of("UTC")).format(formatter);

            User assignUser = teamRequest.getAssignUser();
            List<User> recipients = new ArrayList<>();

            if (assignUser != null) {
                recipients.add(assignUser);
            } else {
                // ✅ If no assigned user, notify all team managers
                recipients = teamService.getTeamManagers(teamRequest.getTeam().getId());
            }

            for (User recipient : recipients) {
                String cacheKey =
                        buildSlaWarningKey(
                                recipient.getId(),
                                teamRequest.getId(),
                                violatingTicket.getTeamRequest().getWorkflow().getId(),
                                violatingTicket.getEventName(),
                                violatingTicket.getToState().getId(),
                                "SendWarningForUpcomingTicketsViolateSlaJob");

                if (deduplicationCacheService.containsKey(cacheKey)) {
                    continue; // Skip if already sent
                }

                String html =
                        p(
                                        text("The ticket "),
                                        a(teamRequest.getRequestTitle())
                                                .withHref(
                                                        "/portal/teams/"
                                                                + Obfuscator.obfuscate(
                                                                        teamRequest
                                                                                .getTeam()
                                                                                .getId())
                                                                + "/requests/"
                                                                + Obfuscator.obfuscate(
                                                                        teamRequest.getId())),
                                        text(
                                                " assigned to your team is approaching its SLA deadline. The SLA is due on "),
                                        strong(text(formattedSlaDueDate)),
                                        text(". Please take necessary action."))
                                .render();

                Notification notification =
                        Notification.builder()
                                .content(html)
                                .type(NotificationType.SLA_WARNING)
                                .user(User.builder().id(recipient.getId()).build())
                                .isRead(false)
                                .build();

                messageTemplate.convertAndSendToUser(
                        String.valueOf(recipient.getId()), "/queue/notifications", notification);

                // ✅ Store Key in Deduplication Cache
                deduplicationCacheService.put(cacheKey, Duration.ofHours(24));
            }

            log.debug("Sending violating ticket {}", violatingTicket);
        }
    }
}
