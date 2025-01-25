package io.flowinquiry.modules.ai.config;

import io.flowinquiry.modules.ai.service.ChatModelService;
import io.flowinquiry.modules.ai.service.OllamaChatModelService;
import io.flowinquiry.modules.ai.service.OpenAiChatModelService;
import java.util.Optional;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class ChatModelConfiguration {

    @Bean
    @Primary
    @ConditionalOnBean({OllamaChatModelService.class, OpenAiChatModelService.class})
    public ChatModelService chatModel(
            Optional<OllamaChatModelService> ollamaChatModelService,
            Optional<OpenAiChatModelService> openAiChatModelService) {
        if (ollamaChatModelService.isPresent()) {
            return ollamaChatModelService.get();
        } else if (openAiChatModelService.isPresent()) {
            return openAiChatModelService.get();
        }

        // If no chat models are present, this block won't execute due to @ConditionalOnBean
        return null;
    }
}
