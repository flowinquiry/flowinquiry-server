package io.flowinquiry.modules.collab.service;

import com.slack.api.Slack;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.methods.response.chat.ChatPostMessageResponse;
import io.flowinquiry.config.FlowInquiryProperties;
import io.flowinquiry.modules.collab.domain.SlackMessage;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Service;

/**
 * This service sends messages to a Slack Workspace. In order to add this component to your
 * dependency container, the SLACK_TOKEN_ID environment variable needs to be set to your workspace
 * token.
 */
@Slf4j
@Service
@ConditionalOnExpression("!'${flowinquiry.slack.token:}'.empty")
public class SlackService {

    private final FlowInquiryProperties flowInquiryProperties;

    public SlackService(FlowInquiryProperties flowInquiryProperties) {
        this.flowInquiryProperties = flowInquiryProperties;
    }

    /**
     * The message in {@link SlackMessage} can be formatted according to the <a
     * href="https://docs.slack.dev/reference/methods/chat.postmessage">Slack docs</a>
     */
    public ChatPostMessageResponse sendSlackMessage(SlackMessage slackMessage)
            throws IOException, SlackApiException {

        Slack slack = Slack.getInstance();

        String slackToken = flowInquiryProperties.getSlack().getToken();
        String message = slackMessage.getMessage();
        String channelId = slackMessage.getChannelId();

        // Build a request object
        ChatPostMessageRequest request =
                ChatPostMessageRequest.builder()
                        .channel(channelId) // Use a channel ID `C1234567` is preferable
                        .text(message)
                        .build();

        ChatPostMessageResponse messageResponse =
                slack.methods(slackToken).chatPostMessage(request);

        if (messageResponse.isOk()) {
            log.debug(
                    "Slack message successfully sent. Message: {} channel: {}", message, channelId);
        } else {
            log.error(
                    "Sending Slack message {} to channel {} failed due to {}",
                    message,
                    channelId,
                    messageResponse.getError());
        }

        return messageResponse;
    }
}
