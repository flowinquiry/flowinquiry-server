package io.flowinquiry.modules.usermanagement.domain;

public enum Permission {
    NONE(0),
    READ(1),
    WRITE(2),
    ACCESS(3);

    private final int code;

    Permission(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static Permission fromCode(int code) {
        for (Permission permission : Permission.values()) {
            if (permission.code == code) {
                return permission;
            }
        }
        throw new IllegalArgumentException("Invalid code for TicketPriority: " + code);
    }
}
