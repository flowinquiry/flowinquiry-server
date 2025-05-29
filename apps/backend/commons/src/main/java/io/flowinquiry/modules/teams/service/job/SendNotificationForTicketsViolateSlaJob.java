package io.flowinquiry.modules.teams.service.job;

import io.flowinquiry.modules.teams.domain.WorkflowTransitionHistory;
import io.flowinquiry.modules.teams.service.WorkflowTransitionHistoryService;
import io.flowinquiry.modules.teams.service.event.TicketViolateSlaEvent;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Profile("!test")
@Component
public class SendNotificationForTicketsViolateSlaJob {

    private final WorkflowTransitionHistoryService workflowTransitionHistoryService;

    private final ApplicationEventPublisher eventPublisher;

    public SendNotificationForTicketsViolateSlaJob(
            WorkflowTransitionHistoryService workflowTransitionHistoryService,
            ApplicationEventPublisher eventPublisher) {
        this.workflowTransitionHistoryService = workflowTransitionHistoryService;
        this.eventPublisher = eventPublisher;
    }

    @Scheduled(cron = "0 0/1 * * * ?")
    @SchedulerLock(name = "SendNotificationForTicketsViolateSlaJob")
    @Transactional
    public void run() {
        List<WorkflowTransitionHistory> violatingTickets =
                workflowTransitionHistoryService.getViolatedTransitions();

        for (WorkflowTransitionHistory violatingTicket : violatingTickets) {
            eventPublisher.publishEvent(new TicketViolateSlaEvent(this, violatingTicket));
            log.debug("SLA violation notification sent for ticket {}", violatingTicket);
        }
    }
}
