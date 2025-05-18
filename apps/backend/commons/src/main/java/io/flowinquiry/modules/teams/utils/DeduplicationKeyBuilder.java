package io.flowinquiry.modules.teams.utils;

/**
 * Utility class for building unique deduplication cache keys related to workflow SLA warnings.
 *
 * <p>This ensures consistent key formatting for notifications, preventing duplicate alerts.
 */
public class DeduplicationKeyBuilder {
    private static final String DELIMITER = ":";

    private DeduplicationKeyBuilder() {
        // Prevent instantiation
    }

    /**
     * Builds a deduplication cache key for SLA warning notifications.
     *
     * @param userId The ID of the recipient user.
     * @param ticketId The ID of the ticket.
     * @param workflowId The ID of the workflow.
     * @param eventName The event name triggering the transition.
     * @param toStateId The ID of the target state.
     * @param type the type of cache key
     * @return A unique, structured cache key.
     */
    public static String buildSlaWarningKey(
            Long userId,
            Long ticketId,
            Long workflowId,
            String eventName,
            Long toStateId,
            String type) {

        return String.join(
                DELIMITER,
                String.valueOf(userId),
                String.valueOf(ticketId),
                String.valueOf(workflowId),
                eventName,
                String.valueOf(toStateId),
                type);
    }
}
