package io.flowinquiry.modules.teams.service.dto;

import com.fasterxml.jackson.annotation.JsonValue;

public enum TicketHealthLevel {
    EXCELLENT,
    GOOD,
    FAIR,
    POOR,
    CRITICAL;

    /**
     * The ticket health level at frontend has the first character is upper case only such as
     * Excellent, Good, etc.
     *
     * @return the json value for the enum
     */
    @JsonValue
    public String toJson() {
        String name = this.name().toLowerCase();
        return Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }
}
