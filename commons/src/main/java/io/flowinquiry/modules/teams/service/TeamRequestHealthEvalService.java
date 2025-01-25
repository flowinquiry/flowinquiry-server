package io.flowinquiry.modules.teams.service;

import io.flowinquiry.modules.teams.domain.TeamRequest;
import io.flowinquiry.modules.teams.domain.TeamRequestConversationHealth;
import io.flowinquiry.modules.teams.repository.TeamRequestConversationHealthRepository;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@ConditionalOnProperty(
        name = {"OPEN_AI_CHAT_MODEL", "OPEN_AI_API_KEY"},
        matchIfMissing = false)
public class TeamRequestHealthEvalService {

    private final TeamRequestConversationHealthRepository teamRequestConversationHealthRepository;
    private final OpenAiChatModel chatModel;

    public TeamRequestHealthEvalService(
            OpenAiChatModel chatModel,
            TeamRequestConversationHealthRepository teamRequestConversationHealthRepository) {
        this.chatModel = chatModel;
        this.teamRequestConversationHealthRepository = teamRequestConversationHealthRepository;
    }

    public String summarizeTeamRequest(String description) {
        return chatModel.call("Summarize this text: " + description);
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
        // Retrieve existing health record or create a new one
        TeamRequestConversationHealth health =
                teamRequestConversationHealthRepository
                        .findByTeamRequestId(teamRequestId)
                        .orElseGet(() -> createNewConversationHealth(teamRequestId, newMessage));

        // Evaluate the sentiment of the new message
        float sentimentScore = evaluateSentiment(newMessage);

        // Check if the new message resolves the issue (only for customer responses)
        boolean resolvesIssue = isCustomerResponse && determineIfResolved(newMessage);

        // Increment metrics
        health.setTotalMessages(health.getTotalMessages() + 1);
        if (isCustomerResponse) {
            health.setTotalQuestions(health.getTotalQuestions() + 1);
            if (resolvesIssue) {
                health.setResolvedQuestions(health.getResolvedQuestions() + 1);
            }
        }

        // Update cumulative sentiment (weigh customer responses more heavily)
        float sentimentWeight = isCustomerResponse ? 1.5f : 1.0f;
        health.setCumulativeSentiment(
                (health.getCumulativeSentiment() * (health.getTotalMessages() - 1)
                                + sentimentScore * sentimentWeight)
                        / health.getTotalMessages());

        // Calculate clarity score
        float clarityScore =
                health.getTotalQuestions() > 0
                        ? (float) health.getResolvedQuestions() / health.getTotalQuestions()
                        : 1.0f;

        // Recalculate conversation health
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

    /**
     * Determines if the new message resolves an issue based on its content.
     *
     * @param newMessage The content of the new message.
     * @return True if the message resolves an issue; otherwise, false.
     */
    private boolean determineIfResolved(String newMessage) {
        // Use OpenAI to analyze if the message resolves an issue
        String prompt =
                "Does this message resolve an issue? Respond with 'yes' or 'no': " + newMessage;
        String response = chatModel.call(prompt);

        // Interpret the response
        return response.trim().equalsIgnoreCase("yes");
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
                chatModel.call(
                        "Evaluate sentiment for this message (return a score between 0 and 1): "
                                + newMessage);

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
        return chatModel.call(prompt);
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
