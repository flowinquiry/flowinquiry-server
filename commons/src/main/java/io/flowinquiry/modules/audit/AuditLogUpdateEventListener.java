package io.flowinquiry.modules.audit;

import io.flowinquiry.modules.collab.domain.ActivityLog;
import io.flowinquiry.modules.collab.domain.EntityType;
import io.flowinquiry.modules.collab.repository.ActivityLogRepository;
import io.flowinquiry.security.SecurityUtils;
import java.lang.reflect.Field;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class AuditLogUpdateEventListener {

    private static final Logger LOG = LoggerFactory.getLogger(AuditLogUpdateEventListener.class);

    private final ActivityLogRepository activityLogRepository;
    private final EntityFieldHandlerRegistryFactory registryFactory;
    private final ApplicationContext applicationContext;

    public AuditLogUpdateEventListener(
            ActivityLogRepository activityLogRepository,
            EntityFieldHandlerRegistryFactory registryFactory,
            ApplicationContext applicationContext) {
        this.activityLogRepository = activityLogRepository;
        this.registryFactory = registryFactory;
        this.applicationContext = applicationContext;
    }

    @Async("asyncTaskExecutor")
    @Transactional
    @EventListener
    public void onNewTeamRequestCreated(AuditLogUpdateEvent event) {
        try {
            Object previousEntity = event.getPreviousEntity();
            Object updatedEntity = event.getUpdatedEntity();
            Class<?> entityClass = updatedEntity.getClass();
            Long entityId = extractEntityId(updatedEntity);

            // Get the registry for the entity
            EntityFieldHandlerRegistry registry = registryFactory.getRegistry(entityClass);

            // Find changes between the existing DTO and updated entity
            List<AuditUtils.FieldChange> changes =
                    AuditUtils.findChanges(previousEntity, updatedEntity, registry);

            if (!changes.isEmpty()) {
                // Generate HTML content
                String htmlLog = ActivityLogUtils.generateHtmlLog(changes);

                // Save the aggregated activity log
                saveActivityLog(registry.getEntityType(), entityId, htmlLog);
            }
        } catch (Exception e) {
            // Log the exception
            LOG.error("Error in async logEntityChanges", e);
        }
    }

    private void saveActivityLog(EntityType entityType, Long entityId, String activityDetails) {
        ActivityLog activityLog = new ActivityLog();
        activityLog.setEntityType(entityType);
        activityLog.setEntityId(entityId);
        activityLog.setContent(activityDetails);
        activityLog.setCreatedBy(SecurityUtils.getCurrentUserAuditorLogin());
        activityLogRepository.save(activityLog);
    }

    private Long extractEntityId(Object entity) {
        try {
            Field idField = entity.getClass().getDeclaredField("id");
            idField.setAccessible(true);
            return (Long) idField.get(entity);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Failed to extract entity ID", e);
        }
    }
}
