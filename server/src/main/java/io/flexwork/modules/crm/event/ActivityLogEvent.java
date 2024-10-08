package io.flexwork.modules.crm.event;

import io.flexwork.modules.crm.domain.ActivityLog;
import org.springframework.context.ApplicationEvent;

public class ActivityLogEvent extends ApplicationEvent {

    private ActivityLog activityLog;

    public ActivityLogEvent(Object source, ActivityLog activityLog) {
        super(source);
        this.activityLog = activityLog;
    }

    public ActivityLog getActivityLog() {
        return activityLog;
    }
}
