package io.flowinquiry.modules.ai.service;

import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(
        name = {"OPEN_AI_CHAT_MODEL", "OPEN_AI_API_KEY"},
        matchIfMissing = false)
public class OpenAiChatModelService implements ChatModelService {

    private final OpenAiChatModel openAiChatModel;

    public OpenAiChatModelService(OpenAiChatModel openAiChatModel) {
        this.openAiChatModel = openAiChatModel;
    }

    @Override
    public String call(String input) {
        return openAiChatModel.call(input);
    }

    @Override
    public String call(Prompt prompt) {
        ChatResponse response = openAiChatModel.call(prompt);
        Generation generation = response.getResult();
        return (generation != null) ? generation.getOutput().getText() : "";
    }
}
