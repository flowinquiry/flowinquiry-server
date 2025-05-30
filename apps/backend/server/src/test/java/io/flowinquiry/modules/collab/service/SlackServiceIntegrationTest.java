package io.flowinquiry.modules.collab.service;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.slack.api.Slack;
import com.slack.api.SlackConfig;
import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.SlackApiException;
import io.flowinquiry.IntegrationTest;
import io.flowinquiry.modules.collab.domain.SlackMessage;
import java.io.IOException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.wiremock.spring.EnableWireMock;

@IntegrationTest
@EnableWireMock
public class SlackServiceIntegrationTest {

    private static WireMockServer mockWebServer;
    private Slack slack;
    private MethodsClient methods;

    @Value("${wiremock.server.baseUrl}")
    private String wireMockUrl;

    private final String token = "xoxb-your-test-token";

    @Autowired private SlackService slackService;

    @BeforeAll
    static void startServer() throws IOException {
        mockWebServer = new WireMockServer();
        mockWebServer.start();
    }

    @AfterAll
    static void shutdown() throws IOException {
        mockWebServer.shutdown();
    }

    @BeforeEach
    void setupSlackClient() {
        SlackConfig config = new SlackConfig();
        config.setProxyUrl(mockWebServer.url("/"));
        config.setPrettyResponseLoggingEnabled(true);
        Slack slack = Slack.getInstance(config);
        methods = slack.methods(token);
    }

    @Test
    void testSendSlackMessage() throws IOException, SlackApiException {
        // Prepare mock response
        stubFor(
                post("/")
                        .willReturn(
                                aResponse()
                                        .withBody(
                                                "{\"ok\":true,\"channel\":\"C123\",\"ts\":\"1234567890.123456\"}")));
        //        mockWebServer.enqueue(new MockResponse()
        //                .setResponseCode(200)
        //
        // .setBody("{\"ok\":true,\"channel\":\"C123\",\"ts\":\"1234567890.123456\"}"));

        System.out.println("Output url: " + wireMockUrl);
        // Act
        SlackMessage slackMessage = new SlackMessage("Message", "ChannelID");
        var response = slackService.sendSlackMessage(slackMessage);

        // Assert
        Assertions.assertTrue(response.isOk());
    }
}
