package io.flowinquiry.modules.teams.domain;

public enum TicketPriority {
    Critical(0),
    High(1),
    Medium(2),
    Low(3),
    Trivial(4);

    private final int code;

    TicketPriority(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static TicketPriority fromCode(int code) {
        for (TicketPriority priority : TicketPriority.values()) {
            if (priority.code == code) {
                return priority;
            }
        }
        throw new IllegalArgumentException("Invalid code for TicketPriority: " + code);
    }
}
