package io.flowinquiry.modules.teams.service;

import static io.flowinquiry.utils.StringUtils.polishedHtmlTagsMessage;

import io.flowinquiry.modules.ai.service.ChatModelService;
import io.flowinquiry.modules.teams.domain.TeamRequest;
import io.flowinquiry.modules.teams.domain.TeamRequestConversationHealth;
import io.flowinquiry.modules.teams.repository.TeamRequestConversationHealthRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@ConditionalOnBean(ChatModelService.class)
public class TeamRequestHealthEvalService {

    private static Logger LOG = LoggerFactory.getLogger(TeamRequestHealthEvalService.class);

    private final TeamRequestConversationHealthRepository teamRequestConversationHealthRepository;
    private final ChatModelService chatModelService;

    public TeamRequestHealthEvalService(
            ChatModelService chatModelService,
            TeamRequestConversationHealthRepository teamRequestConversationHealthRepository) {
        this.chatModelService = chatModelService;
        this.teamRequestConversationHealthRepository = teamRequestConversationHealthRepository;
    }

    public String summarizeTeamRequest(String description) {
        return chatModelService.call("Summarize this text: " + description);
    }

    /**
     * Evaluates the conversation health incrementally by updating metrics.
     *
     * @param teamRequestId The ID of the team request.
     * @param newMessage The new message in the conversation.
     */
    @Transactional
    public void evaluateConversationHealth(
            Long teamRequestId, String newMessage, boolean isCustomerResponse) {

        String polishedMessage = polishedHtmlTagsMessage(newMessage);

        // Retrieve existing health record or create a new one
        TeamRequestConversationHealth health =
                teamRequestConversationHealthRepository
                        .findByTeamRequestId(teamRequestId)
                        .orElseGet(
                                () -> createNewConversationHealth(teamRequestId, polishedMessage));

        // Evaluate the sentiment of the new message
        float sentimentScore = evaluateSentiment(polishedMessage);
        LOG.debug("Message '{}' has sentiment score: {}", polishedMessage, sentimentScore);

        // Check if the message resolves the issue (only for customer responses)
        boolean isMessageResolved = determineIfResolved(polishedMessage);
        boolean resolvesIssue = isCustomerResponse && isMessageResolved;
        LOG.debug("Message resolved: {}, Customer response: {}", resolvesIssue, isCustomerResponse);

        // Increment total messages
        health.setTotalMessages(health.getTotalMessages() + 1);

        // Check if the customer response contains a question using AI
        if (isCustomerResponse && isQuestion(polishedMessage)) {
            health.setTotalQuestions(health.getTotalQuestions() + 1);
        }

        // Increment resolved questions only if it resolves a previously asked question
        if (resolvesIssue) {
            health.setResolvedQuestions(health.getResolvedQuestions() + 1);
        }

        // Update cumulative sentiment with weight for customer responses
        float sentimentWeight = isCustomerResponse ? 1.5f : 1.0f;
        float currentCumulativeSentiment = health.getCumulativeSentiment();
        health.setCumulativeSentiment(
                (currentCumulativeSentiment * (health.getTotalMessages() - 1)
                                + sentimentScore * sentimentWeight)
                        / health.getTotalMessages());

        // Calculate clarity score, handling cases where no questions have been asked
        float clarityScore =
                health.getTotalQuestions() > 0
                        ? (float) health.getResolvedQuestions() / health.getTotalQuestions()
                        : 1.0f;

        // Adjust conversation health calculation
        health.setConversationHealth(
                (0.4f * health.getCumulativeSentiment())
                        + // Sentiment contribution
                        (0.4f * clarityScore)
                        + // Clarity contribution
                        (0.2f * (resolvesIssue ? 1.0f : 0.0f)) // Resolution contribution
                );

        // Save the updated health record
        teamRequestConversationHealthRepository.save(health);
    }

    /** Determines if the message contains a question using AI. */
    private boolean isQuestion(String message) {
        String aiPrompt =
                "Determine if the following message is a question. Respond with 'true' or 'false':\n\nMessage: "
                        + message;
        String aiResponse = chatModelService.call(aiPrompt); // Example AI service integration
        return Boolean.parseBoolean(aiResponse.trim());
    }

    /** Determines if the message resolves the issue. */
    private boolean determineIfResolved(String message) {
        // AI-assisted resolution determination (or custom logic can be implemented here)
        String aiPrompt =
                "Does the following message indicate that the issue has been resolved? Respond with 'true' or 'false':\n\nMessage: "
                        + message;
        String aiResponse = chatModelService.call(aiPrompt); // Example AI service integration
        return Boolean.parseBoolean(aiResponse.trim());
    }

    /**
     * Calls OpenAI to evaluate the sentiment of a message.
     *
     * @param newMessage The message to evaluate.
     * @return A sentiment score (0.0 - 1.0).
     */
    private float evaluateSentiment(String newMessage) {
        // Use OpenAI to analyze sentiment
        String response =
                chatModelService.call(
                        new Prompt(
                                "Evaluate the sentiment of this message and return a score between 0 and 1. Provide only the number: "
                                        + newMessage,
                                OpenAiChatOptions.builder()
                                        .temperature(0.1)
                                        .maxCompletionTokens(10)
                                        .build()));

        // Extract the sentiment score from the response (assuming response contains a parsable
        // float)
        try {
            return Float.parseFloat(response.trim());
        } catch (NumberFormatException e) {
            throw new IllegalStateException(
                    "Unable to parse sentiment score from OpenAI response: " + response);
        }
    }

    /**
     * Generates a summary for the team request content using OpenAI.
     *
     * @param description The initial description of the team request.
     * @return The generated summary.
     */
    private String generateSummary(String description) {
        String prompt = "Summarize this text: " + description;
        return chatModelService.call(prompt);
    }

    /**
     * Creates a new conversation health record for a team request.
     *
     * @param teamRequestId The ID of the team request.
     * @return The newly created conversation health entity.
     */
    private TeamRequestConversationHealth createNewConversationHealth(
            Long teamRequestId, String firstMessage) {
        TeamRequestConversationHealth health = new TeamRequestConversationHealth();
        health.setTeamRequest(TeamRequest.builder().id(teamRequestId).build());
        health.setCumulativeSentiment(0.0f);
        health.setTotalMessages(0);
        health.setTotalQuestions(0);
        health.setResolvedQuestions(0);
        health.setConversationHealth(0.0f);
        health.setSummary(generateSummary(firstMessage));
        return teamRequestConversationHealthRepository.save(health);
    }
}
