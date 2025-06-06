package io.flowinquiry.modules.teams.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum WorkflowVisibility {
    PUBLIC,
    PRIVATE,
    TEAM;

    private static final Logger log = LoggerFactory.getLogger(WorkflowVisibility.class);

    public static WorkflowVisibility from(String value) {
        try {
            return WorkflowVisibility.valueOf(value);
        } catch (Exception e) {
            log.warn("Invalid value for WorkflowVisibility: {}", value);
            return WorkflowVisibility.PRIVATE;
        }
    }
}
