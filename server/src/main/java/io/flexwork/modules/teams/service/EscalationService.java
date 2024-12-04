package io.flexwork.modules.teams.service;

import io.flexwork.modules.collab.service.NotificationService;
import io.flexwork.modules.teams.domain.TeamRequest;
import io.flexwork.modules.teams.repository.EscalationTrackingRepository;
import io.flexwork.modules.teams.repository.TeamRequestRepository;
import io.flexwork.modules.teams.repository.WorkflowRepository;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EscalationService {

    private final TeamRequestRepository teamRequestRepository;
    private final EscalationTrackingRepository escalationTrackingRepository;
    private final WorkflowRepository workflowRepository;
    private final NotificationService notificationService;

    public EscalationService(
            TeamRequestRepository teamRequestRepository,
            EscalationTrackingRepository escalationTrackingRepository,
            WorkflowRepository workflowRepository,
            NotificationService notificationService) {
        this.teamRequestRepository = teamRequestRepository;
        this.escalationTrackingRepository = escalationTrackingRepository;
        this.workflowRepository = workflowRepository;
        this.notificationService = notificationService;
    }

    public void escalateTicketsAtLevel(int level) {
        List<Long> workflowIds = teamRequestRepository.findAllWorkflowIds();

        for (Long workflowId : workflowIds) {
            // Get the timeout for the escalation level from the workflow
            Integer timeoutInMinutes =
                    workflowRepository
                            .findEscalationTimeoutByLevel(workflowId, level)
                            .orElseThrow(
                                    () ->
                                            new IllegalStateException(
                                                    "Timeout not configured for escalation level "
                                                            + level
                                                            + " in workflow "
                                                            + workflowId));

            LocalDateTime escalationTimeThreshold =
                    LocalDateTime.now().minusMinutes(timeoutInMinutes);

            // Find tickets that have exceeded the timeout for this level
            //            List<Long> tickets =
            //                    teamRequestRepository.findTicketsExceedingSlaAndLevel(
            //                            workflowId, level, escalationTimeThreshold);
            //            tickets.forEach(this::escalateIfNoAction);
        }
    }

    @Transactional
    public void escalateIfNoAction(Long teamRequestId) {
        TeamRequest teamRequest =
                teamRequestRepository
                        .findById(teamRequestId)
                        .orElseThrow(() -> new EntityNotFoundException("Team Request not found"));

        int currentLevel =
                escalationTrackingRepository.findMaxEscalationLevel(teamRequestId).orElse(0);

        Long nextEscalateUserId = getNextEscalationUser(teamRequest, currentLevel + 1);
        if (nextEscalateUserId == null) {
            notifyAdminForManualIntervention(teamRequestId);
            return;
        }

        // Add escalation tracking entry
        //        EscalationTracking newEscalation = new EscalationTracking();
        //        newEscalation.setTeamRequestId(teamRequestId);
        //        newEscalation.setEscalationLevel(currentLevel + 1);
        //        newEscalation.setEscalatedToUserId(nextEscalateUserId);
        //        escalationTrackingRepository.save(newEscalation);
        //
        //        // Notify the next user
        //        notificationService.notifyEscalation(teamRequestId, nextEscalateUserId,
        // currentLevel + 1);
    }

    private Long getNextEscalationUser(TeamRequest teamRequest, int escalationLevel) {
        //        switch (escalationLevel) {
        //            case 1:
        //                return teamRequest.getTeam().getManagerId();
        //            case 2:
        //                return teamRequest.getTeam().getOrganization() != null
        //                        ? teamRequest.getTeam().getOrganization().getManagerId()
        //                        : null;
        //            default:
        //                return null; // No further escalation available
        //        }
        return null;
    }

    private void notifyAdminForManualIntervention(Long teamRequestId) {
        //        notificationService.notifyAdmin(
        //                String.format("Team Request %d has reached the final escalation level
        // without resolution.", teamRequestId)
        //        );
    }
}
