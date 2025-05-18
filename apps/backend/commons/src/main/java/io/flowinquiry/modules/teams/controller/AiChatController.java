package io.flowinquiry.modules.teams.controller;

import io.flowinquiry.modules.teams.service.TicketHealthEvalService;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai")
@ConditionalOnBean(OpenAiChatModel.class)
public class AiChatController {

    private final TicketHealthEvalService ticketHealthEvalService;

    public AiChatController(TicketHealthEvalService ticketHealthEvalService) {
        this.ticketHealthEvalService = ticketHealthEvalService;
    }

    @PostMapping
    public String createRequestSummary(@RequestBody String requestDescription) {
        return ticketHealthEvalService.summarizeTeamRequest(requestDescription);
    }
}
