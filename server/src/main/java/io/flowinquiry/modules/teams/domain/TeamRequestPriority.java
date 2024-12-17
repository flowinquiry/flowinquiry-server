package io.flowinquiry.modules.teams.domain;

public enum TeamRequestPriority {
    Critical(0),
    High(1),
    Medium(2),
    Low(3),
    Trivial(4);

    private final int code;

    TeamRequestPriority(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static TeamRequestPriority fromCode(int code) {
        for (TeamRequestPriority priority : TeamRequestPriority.values()) {
            if (priority.code == code) {
                return priority;
            }
        }
        throw new IllegalArgumentException("Invalid code for TeamRequestPriority: " + code);
    }
}
