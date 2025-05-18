package io.flowinquiry.modules.teams.service.listener;

import io.flowinquiry.modules.collab.service.dto.CommentDTO;
import io.flowinquiry.modules.teams.service.TicketHealthEvalService;
import io.flowinquiry.modules.teams.service.TicketService;
import io.flowinquiry.modules.teams.service.dto.TicketDTO;
import io.flowinquiry.modules.teams.service.event.TicketCommentCreatedEvent;
import java.util.Objects;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnBean(TicketHealthEvalService.class)
public class TicketCommentCreatedAiEvaluateConversationHealthEventListener {

    private final TicketService ticketService;

    private final TicketHealthEvalService ticketHealthEvalService;

    public TicketCommentCreatedAiEvaluateConversationHealthEventListener(
            TicketService ticketService, TicketHealthEvalService ticketHealthEvalService) {
        this.ticketHealthEvalService = ticketHealthEvalService;
        this.ticketService = ticketService;
    }

    @Async("asyncTaskExecutor")
    @EventListener
    public void onTeamRequestNewCommentAiEvaluateConversationHealthEvent(
            TicketCommentCreatedEvent event) {
        CommentDTO comment = event.getCommentDTO();
        TicketDTO ticketDTO = ticketService.getTeamRequestById(comment.getEntityId());
        ticketHealthEvalService.evaluateConversationHealth(
                comment.getEntityId(),
                comment.getContent(),
                Objects.equals(ticketDTO.getRequestUserId(), comment.getCreatedById()));
    }
}
