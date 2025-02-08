package io.flowinquiry.modules.teams.service.listener;

import io.flowinquiry.modules.teams.service.TeamRequestHealthEvalService;
import io.flowinquiry.modules.teams.service.TeamRequestService;
import io.flowinquiry.modules.teams.service.event.TeamRequestCommentCreatedEvent;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnBean(TeamRequestHealthEvalService.class)
public class TeamRequestCommentCreatedAiEvaluateConversationHealthEventListener {

    private final TeamRequestService teamRequestService;

    private final TeamRequestHealthEvalService teamRequestHealthEvalService;

    public TeamRequestCommentCreatedAiEvaluateConversationHealthEventListener(
            TeamRequestService teamRequestService,
            TeamRequestHealthEvalService teamRequestHealthEvalService) {
        this.teamRequestHealthEvalService = teamRequestHealthEvalService;
        this.teamRequestService = teamRequestService;
    }

    @Async("asyncTaskExecutor")
    @EventListener
    public void onTeamRequestNewCommentAiEvaluateConversationHealthEvent(
            TeamRequestCommentCreatedEvent event) {
        //        CommentDTO comment = event.getCommentDTO();
        //        TeamRequestDTO teamRequestDTO =
        //                teamRequestService.getTeamRequestById(comment.getEntityId());
        //        teamRequestHealthEvalService.evaluateConversationHealth(
        //                comment.getEntityId(),
        //                comment.getContent(),
        //                Objects.equals(teamRequestDTO.getRequestUserId(),
        // comment.getCreatedById()));
    }
}
