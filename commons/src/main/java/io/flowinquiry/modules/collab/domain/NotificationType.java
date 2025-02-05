package io.flowinquiry.modules.collab.domain;

public enum NotificationType {
    INFO, // General information (e.g., "You have a new task")
    WARNING, // Warnings (e.g., "Your request is pending approval")
    ERROR, // Errors (e.g., "Failed to update request")
    SLA_BREACH, // SLA time limit exceeded, urgent action needed
    SLA_WARNING, // SLA deadline approaching
    ESCALATION_NOTICE, // Request escalated due to SLA breach
}
