package io.flowinquiry.modules.collab.service;

import com.slack.api.Slack;
import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.SlackApiException;

import java.io.IOException;

import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.methods.response.chat.ChatPostMessageResponse;
import io.flowinquiry.IntegrationTest;
import io.flowinquiry.modules.collab.domain.SlackMessage;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@IntegrationTest
@TestPropertySource(properties = "flowinquiry.slack.token=xoxb-your-test-token")
public class SlackServiceIT {

    @Mock
    private Slack slack;
    @Mock
    private MethodsClient methods;
    @Mock
    private ChatPostMessageResponse messageResponse;

    private final String token = "xoxb-your-test-token";

    @Autowired
    private SlackService slackService;

    @Test
    void SendSlackMessage_HappyPath() throws IOException, SlackApiException {

        //Arrange (Prepare mocks)
        try(MockedStatic<Slack> mockedStaticSlack = Mockito.mockStatic(Slack.class)) {

            mockedStaticSlack.when(Slack::getInstance).thenReturn(slack);
            when(slack.methods(token)).thenReturn(methods);

            final String EXPECTED_CHAN_ID = "ChannelID";
            final String EXPECTED_MESSAGE = "MESSAGE";
            final ChatPostMessageRequest EXPECTED_SLACK_REQUEST =
                    ChatPostMessageRequest.builder()
                            .channel(EXPECTED_CHAN_ID) // Use a channel ID `C1234567` is preferable
                            .text(EXPECTED_MESSAGE)
                            .build();
            when(messageResponse.isOk()).thenReturn(true);
            when(methods.chatPostMessage(any(ChatPostMessageRequest.class))).thenReturn(messageResponse);

            final ArgumentCaptor<ChatPostMessageRequest> messageRequestArgCaptor =
                    ArgumentCaptor.forClass(ChatPostMessageRequest.class);
            SlackMessage slackMessage = new SlackMessage(EXPECTED_MESSAGE, EXPECTED_CHAN_ID);

            // Act
            ChatPostMessageResponse actualMessageResponse = slackService.sendSlackMessage(slackMessage);

            // Assert
            mockedStaticSlack.verify(Slack::getInstance);
            verify(slack).methods(any());
            verify(methods).chatPostMessage(messageRequestArgCaptor.capture());

            ChatPostMessageRequest actualMessageRequest = messageRequestArgCaptor.getValue();
            Assertions.assertEquals(EXPECTED_SLACK_REQUEST, actualMessageRequest);
            Assertions.assertTrue(actualMessageResponse.isOk());
        }
    }

    @Test
    void SendSlackMessage_UnHappyPath() throws IOException, SlackApiException {

        //Arrange (Prepare mocks)
        try(MockedStatic<Slack> mockedStaticSlack = Mockito.mockStatic(Slack.class)) {

            mockedStaticSlack.when(Slack::getInstance).thenReturn(slack);
            when(slack.methods(token)).thenReturn(methods);

            final String EXPECTED_CHAN_ID = "ChannelID";
            final String EXPECTED_MESSAGE = "MESSAGE";
            final ChatPostMessageRequest EXPECTED_SLACK_REQUEST =
                    ChatPostMessageRequest.builder()
                            .channel(EXPECTED_CHAN_ID) // Use a channel ID `C1234567` is preferable
                            .text(EXPECTED_MESSAGE)
                            .build();
            when(messageResponse.isOk()).thenReturn(false);
            when(methods.chatPostMessage(any(ChatPostMessageRequest.class))).thenReturn(messageResponse);

            final ArgumentCaptor<ChatPostMessageRequest> messageRequestArgCaptor =
                    ArgumentCaptor.forClass(ChatPostMessageRequest.class);
            SlackMessage slackMessage = new SlackMessage(EXPECTED_MESSAGE, EXPECTED_CHAN_ID);

            // Act
            ChatPostMessageResponse actualMessageResponse = slackService.sendSlackMessage(slackMessage);

            // Assert
            mockedStaticSlack.verify(Slack::getInstance);
            verify(slack).methods(any());
            verify(methods).chatPostMessage(messageRequestArgCaptor.capture());

            ChatPostMessageRequest actualMessageRequest = messageRequestArgCaptor.getValue();
            Assertions.assertEquals(EXPECTED_SLACK_REQUEST, actualMessageRequest);
            Assertions.assertFalse(actualMessageResponse.isOk());
        }
    }

    @Test
    void SendSlackMessage_UnHappyPath_ThrowsIOException() throws IOException, SlackApiException {

        //Arrange (Prepare mocks)
        try(MockedStatic<Slack> mockedStaticSlack = Mockito.mockStatic(Slack.class)) {

            mockedStaticSlack.when(Slack::getInstance).thenReturn(slack);
            when(slack.methods(token)).thenReturn(methods);

            final String EXPECTED_CHAN_ID = "ChannelID";
            final String EXPECTED_MESSAGE = "MESSAGE";
            final ChatPostMessageRequest EXPECTED_SLACK_REQUEST =
                    ChatPostMessageRequest.builder()
                            .channel(EXPECTED_CHAN_ID) // Use a channel ID `C1234567` is preferable
                            .text(EXPECTED_MESSAGE)
                            .build();
            when(messageResponse.isOk()).thenReturn(false);
            when(methods.chatPostMessage(any(ChatPostMessageRequest.class))).thenThrow(IOException.class);

            final ArgumentCaptor<ChatPostMessageRequest> messageRequestArgCaptor =
                    ArgumentCaptor.forClass(ChatPostMessageRequest.class);
            SlackMessage slackMessage = new SlackMessage(EXPECTED_MESSAGE, EXPECTED_CHAN_ID);

            // Act & Assert
            Assertions.assertThrows(IOException.class, () -> slackService.sendSlackMessage(slackMessage));

            mockedStaticSlack.verify(Slack::getInstance);
            verify(slack).methods(any());
            verify(methods).chatPostMessage(messageRequestArgCaptor.capture());

            ChatPostMessageRequest actualMessageRequest = messageRequestArgCaptor.getValue();
            Assertions.assertEquals(EXPECTED_SLACK_REQUEST, actualMessageRequest);
        }
    }

    @Test
    void SendSlackMessage_UnHappyPath_ThrowsSlackApiException() throws IOException, SlackApiException {

        //Arrange (Prepare mocks)
        try(MockedStatic<Slack> mockedStaticSlack = Mockito.mockStatic(Slack.class)) {

            mockedStaticSlack.when(Slack::getInstance).thenReturn(slack);
            when(slack.methods(token)).thenReturn(methods);

            final String EXPECTED_CHAN_ID = "ChannelID";
            final String EXPECTED_MESSAGE = "MESSAGE";
            final ChatPostMessageRequest EXPECTED_SLACK_REQUEST =
                    ChatPostMessageRequest.builder()
                            .channel(EXPECTED_CHAN_ID) // Use a channel ID `C1234567` is preferable
                            .text(EXPECTED_MESSAGE)
                            .build();
            when(messageResponse.isOk()).thenReturn(false);
            when(methods.chatPostMessage(any(ChatPostMessageRequest.class))).thenThrow(SlackApiException.class);

            final ArgumentCaptor<ChatPostMessageRequest> messageRequestArgCaptor =
                    ArgumentCaptor.forClass(ChatPostMessageRequest.class);
            SlackMessage slackMessage = new SlackMessage(EXPECTED_MESSAGE, EXPECTED_CHAN_ID);

            // Act & Assert
            Assertions.assertThrows(SlackApiException.class, () -> slackService.sendSlackMessage(slackMessage));

            mockedStaticSlack.verify(Slack::getInstance);
            verify(slack).methods(any());
            verify(methods).chatPostMessage(messageRequestArgCaptor.capture());

            ChatPostMessageRequest actualMessageRequest = messageRequestArgCaptor.getValue();
            Assertions.assertEquals(EXPECTED_SLACK_REQUEST, actualMessageRequest);
        }
    }
}
