package io.flexwork.modules.crm.event;

import io.flexwork.modules.crm.repository.ActivityLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class ActivityLogListener {

    private static final Logger LOG = LoggerFactory.getLogger(ActivityLogListener.class);

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
