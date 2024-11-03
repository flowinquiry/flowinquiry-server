package io.flexwork.modules.crm.event;

import io.flexwork.modules.crm.repository.ActivityLogRepository;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class ActivityLogListener {

    private ActivityLogRepository activityLogRepository;

    public ActivityLogListener(ActivityLogRepository activityLogRepository) {
        this.activityLogRepository = activityLogRepository;
    }

    @Async
    @EventListener
    public void handleActivityLogEvent(ActivityLogEvent event) {
        activityLogRepository.save(event.getActivityLog());
    }
}
