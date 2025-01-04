package io.flowinquiry.modules.teams.service.listener;

import io.flowinquiry.config.FlowInquiryProperties;
import io.flowinquiry.modules.collab.service.MailService;
import io.flowinquiry.modules.teams.service.dto.TeamRequestDTO;
import io.flowinquiry.modules.teams.service.event.NewTeamRequestCreatedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class NewTeamRequestCreatedMailEventListener {

    private final MailService mailService;
    private final FlowInquiryProperties flowInquiryProperties;

    public NewTeamRequestCreatedMailEventListener(
            MailService mailService, FlowInquiryProperties flowInquiryProperties) {
        this.mailService = mailService;
        this.flowInquiryProperties = flowInquiryProperties;
    }

    @Async("auditLogExecutor")
    @Transactional
    @EventListener
    public void onNewTeamRequestCreated(NewTeamRequestCreatedEvent event) {
        TeamRequestDTO teamRequestDTO = event.getTeamRequest();
    }
}
