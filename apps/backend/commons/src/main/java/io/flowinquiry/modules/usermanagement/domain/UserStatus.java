package io.flowinquiry.modules.usermanagement.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum UserStatus {
    ACTIVE,
    PENDING;

    private static final Logger log = LoggerFactory.getLogger(UserStatus.class);

    public static UserStatus from(String value) {
        try {
            return UserStatus.valueOf(value);
        } catch (Exception e) {
            log.warn("Invalid value for WorkflowVisibility: {}", value);
            return UserStatus.ACTIVE;
        }
    }
}
