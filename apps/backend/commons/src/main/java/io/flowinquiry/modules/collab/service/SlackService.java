package io.flowinquiry.modules.collab.service;

import com.slack.api.Slack;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.methods.response.chat.ChatPostMessageResponse;
import io.flowinquiry.config.FlowInquiryProperties;
import io.flowinquiry.modules.collab.domain.SlackMessage;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SlackService {

    private final FlowInquiryProperties flowInquiryProperties;

    private final Slack slack = Slack.getInstance();

    public SlackService(FlowInquiryProperties flowInquiryProperties) {
        this.flowInquiryProperties = flowInquiryProperties;
    }

    public void sendSlackMessage(SlackMessage slackMessage) {
        String slackToken = flowInquiryProperties.getSlack().getToken();
        String message = slackMessage.getMessage();
        String team = slackMessage.getTeam();
        String channel = flowInquiryProperties.getSlack().getTeamChannelMap().get(team);

        if (channel == null) {
            log.error(
                    "Unable to send a Slack message to team {}, as team channel is missing in configurations.",
                    team);
            return;
        }

        // Build a request object
        ChatPostMessageRequest request =
                ChatPostMessageRequest.builder()
                        .channel(channel) // Use a channel ID `C1234567` is preferable
                        .text(message)
                        .build();

        try {
            ChatPostMessageResponse messageResponse =
                    slack.methods(slackToken).chatPostMessage(request);
            if (messageResponse.isOk()) {
                log.info(
                        "Slack message successfully sent. message: {} channel: {}",
                        message,
                        channel);
            } else {
                log.error(
                        "Sending Slack message {} to channel {} failed due to {}",
                        message,
                        channel,
                        messageResponse.getError());
            }
        } catch (IOException | SlackApiException e) {
            log.error("Error sending Slack message {} to channel {}", message, channel, e);
        }
    }
}
