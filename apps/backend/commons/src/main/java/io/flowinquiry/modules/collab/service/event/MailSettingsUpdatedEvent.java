package io.flowinquiry.modules.collab.service.event;

import org.springframework.context.ApplicationEvent;

public class MailSettingsUpdatedEvent extends ApplicationEvent {
    public MailSettingsUpdatedEvent(Object source) {
        super(source);
    }
}
