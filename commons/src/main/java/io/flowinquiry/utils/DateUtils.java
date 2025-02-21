package io.flowinquiry.utils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class DateUtils {
    /**
     * Parses a date range string like "7d", "30d" and returns the corresponding Instant. If the
     * input is null or invalid, it throws an exception.
     *
     * @param range The range string (e.g., "7d" for 7 days ago)
     * @return The calculated Instant (truncated to midnight UTC)
     */
    public static Instant parseDateRange(String range) {
        if (range == null || range.isBlank()) {
            return null;
        }
        if (range.matches("\\d+d")) { // Match format like "7d", "30d"
            int days = Integer.parseInt(range.replace("d", ""));
            return Instant.now().minus(days, ChronoUnit.DAYS).truncatedTo(ChronoUnit.DAYS);
        }
        throw new IllegalArgumentException("Invalid range format. Use format like '7d' or '30d'.");
    }

    /**
     * Ensures an Instant is truncated to midnight (UTC). Returns null if input is null.
     *
     * @param instant The input Instant
     * @return The Instant at midnight UTC
     */
    public static Instant truncateToMidnight(Instant instant) {
        return (instant != null) ? instant.truncatedTo(ChronoUnit.DAYS) : null;
    }
}
