package io.flowinquiry.modules.teams.service;

import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnBean(OpenAiChatModel.class)
public class TeamRequestHealthEvalService {

    private final OpenAiChatModel chatModel;

    public TeamRequestHealthEvalService(OpenAiChatModel chatModel) {
        this.chatModel = chatModel;
    }

    public String summarizeTeamRequest(String description) {
        return chatModel.call("Summarize this text: " + description);
    }
}
