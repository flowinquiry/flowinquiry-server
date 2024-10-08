package io.flexwork.modules.crm.service;

import io.flexwork.modules.crm.event.ActivityLogEvent;
import java.util.function.Function;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class AuditServiceWrapperService<T> {

    @Autowired private ApplicationEventPublisher eventPublisher;

    public T saveEntity(
            T entity,
            JpaRepository<T, Long> repository,
            Function<T, ActivityLogEvent> eventBuilder) {
        T savedEntity = repository.save(entity);

        // Build and publish custom activity log event
        ActivityLogEvent activityLogEvent = eventBuilder.apply(savedEntity);
        eventPublisher.publishEvent(activityLogEvent);
        return savedEntity;
    }
}
