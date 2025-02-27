package io.flowinquiry.modules.teams.service.listener;

import io.flowinquiry.modules.teams.service.WorkflowService;
import io.flowinquiry.modules.teams.service.dto.TeamDTO;
import io.flowinquiry.modules.teams.service.dto.WorkflowDTO;
import io.flowinquiry.modules.teams.service.event.NewTeamCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NewTeamCreatedEventListener {

    private final WorkflowService workflowService;

    public NewTeamCreatedEventListener(WorkflowService workflowService) {
        this.workflowService = workflowService;
    }

    @Async("asyncTaskExecutor")
    @EventListener
    public void onNewTeamCreated(NewTeamCreatedEvent event) {
        TeamDTO teamDTO = event.getTeam();

        WorkflowDTO globalWorkflowUsedForProject =
                workflowService.getGlobalWorkflowUsedForProject();
        WorkflowDTO teamProjectWorkflow =
                WorkflowDTO.builder()
                        .name("Project workflow")
                        .description("Workflow for project management")
                        .requestName("Ticket")
                        .useForProject(true)
                        .build();
        workflowService.createWorkflowByCloning(
                teamDTO.getId(), globalWorkflowUsedForProject.getId(), teamProjectWorkflow);
    }
}
