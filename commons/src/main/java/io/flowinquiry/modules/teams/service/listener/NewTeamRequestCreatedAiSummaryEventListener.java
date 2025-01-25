package io.flowinquiry.modules.teams.service.listener;

import io.flowinquiry.modules.teams.service.TeamRequestHealthEvalService;
import io.flowinquiry.modules.teams.service.dto.TeamRequestDTO;
import io.flowinquiry.modules.teams.service.event.NewTeamRequestCreatedEvent;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnBean(TeamRequestHealthEvalService.class)
public class NewTeamRequestCreatedAiSummaryEventListener {

    private final TeamRequestHealthEvalService teamRequestHealthEvalService;

    public NewTeamRequestCreatedAiSummaryEventListener(
            TeamRequestHealthEvalService teamRequestHealthEvalService) {
        this.teamRequestHealthEvalService = teamRequestHealthEvalService;
    }

    @Async("asyncTaskExecutor")
    @EventListener
    public void onNewTeamRequestCreated(NewTeamRequestCreatedEvent event) {
        TeamRequestDTO teamRequestDTO = event.getTeamRequest();
        teamRequestHealthEvalService.evaluateConversationHealth(
                teamRequestDTO.getId(),
                "Title: "
                        + teamRequestDTO.getRequestTitle()
                        + "\n"
                        + "Description: "
                        + teamRequestDTO.getRequestDescription()
                        + "\n",
                true);
    }
}
