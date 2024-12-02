package io.flexwork.modules.teams.service.listener;

import io.flexwork.modules.teams.service.WorkflowTransitionHistoryService;
import io.flexwork.modules.teams.service.event.TeamRequestWorkStateTransitionEvent;
import jakarta.transaction.Transactional;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class TeamRequestWorkStateTransitionEventListener {

    private final WorkflowTransitionHistoryService workflowTransitionHistoryService;

    public TeamRequestWorkStateTransitionEventListener(
            WorkflowTransitionHistoryService workflowTransitionHistoryService) {
        this.workflowTransitionHistoryService = workflowTransitionHistoryService;
    }

    @Async("auditLogExecutor")
    @EventListener
    @Transactional
    public void onWorkflowStateTransition(TeamRequestWorkStateTransitionEvent event) {
        workflowTransitionHistoryService.recordWorkflowTransitionHistory(
                event.getTeamRequestId(), event.getSourceStateId(), event.getTargetStateId());
    }
}
