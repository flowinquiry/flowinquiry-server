package io.flexwork.modules.audit;

import io.flexwork.modules.collab.domain.ActivityLog;
import io.flexwork.modules.collab.domain.EntityType;
import io.flexwork.modules.collab.repository.ActivityLogRepository;
import jakarta.persistence.EntityNotFoundException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class AuditLogAsyncService {

    private static final Logger LOG = LoggerFactory.getLogger(AuditLogAsyncService.class);

    private final ActivityLogRepository activityLogRepository;
    private final EntityFieldHandlerRegistryFactory registryFactory;
    private final ApplicationContext applicationContext;

    public AuditLogAsyncService(
            ActivityLogRepository activityLogRepository,
            EntityFieldHandlerRegistryFactory registryFactory,
            ApplicationContext applicationContext) {
        this.activityLogRepository = activityLogRepository;
        this.registryFactory = registryFactory;
        this.applicationContext = applicationContext;
    }

    @Async
    public void handleAsyncLogEntityChanges(ProceedingJoinPoint joinPoint) {
        try {
            Object[] args = joinPoint.getArgs();
            Object updatedEntity = args[0];
            Class<?> entityClass = updatedEntity.getClass();
            Long entityId = extractEntityId(updatedEntity);

            // Fetch the existing entity
            Object existingEntity = fetchExistingEntity(entityClass, entityId);

            // Convert existing entity to DTO form using the mapper
            Object existingEntityDto = mapEntityToDto(existingEntity.getClass(), existingEntity);

            // Get the registry for the entity
            EntityFieldHandlerRegistry registry = registryFactory.getRegistry(entityClass);

            // Find changes between the existing DTO and updated entity
            List<AuditUtils.FieldChange> changes =
                    AuditUtils.findChanges(existingEntityDto, updatedEntity, registry);

            if (!changes.isEmpty()) {
                // Generate HTML content
                String htmlLog =
                        ActivityLogUtils.generateHtmlLog(
                                registry.getEntityType(), entityId, changes);

                // Save the aggregated activity log
                saveActivityLog(registry.getEntityType(), entityId, htmlLog);
            }
        } catch (Exception e) {
            // Log the exception
            LOG.error("Error in async logEntityChanges", e);
        }
    }

    private Object mapEntityToDto(Class<?> entityClass, Object entity) {
        try {
            // Derive the mapper bean name based on conventions (e.g., "teamMapper")
            String mapperBeanName = getMapperBeanName(entityClass);

            // Fetch the mapper bean from the Spring context
            Object mapper = applicationContext.getBean(mapperBeanName);

            // Find the "toDto" method in the mapper
            Method toDtoMethod = mapper.getClass().getDeclaredMethod("toDto", entityClass);

            // Invoke the "toDto" method and return the result
            return toDtoMethod.invoke(mapper, entity);
        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to map entity to DTO for class: " + entityClass.getSimpleName(), e);
        }
    }

    private String getMapperBeanName(Class<?> entityClass) {
        // Get the simple name of the entity class
        String entityName = entityClass.getSimpleName();

        // Remove the "DTO" suffix if it exists
        if (entityName.endsWith("DTO")) {
            entityName = entityName.substring(0, entityName.length() - 3);
        }

        // Convert the entity name to the mapper bean name
        String mapperBeanName =
                Character.toLowerCase(entityName.charAt(0))
                        + entityName.substring(1)
                        + "MapperImpl";

        return mapperBeanName;
    }

    private void saveActivityLog(EntityType entityType, Long entityId, String activityDetails) {
        ActivityLog activityLog = new ActivityLog();
        activityLog.setEntityType(entityType);
        activityLog.setEntityId(entityId);
        activityLog.setContent(activityDetails);

        activityLogRepository.save(activityLog);
    }

    private Object fetchExistingEntity(Class<?> entityClass, Long entityId) {
        // Get the repository bean name for the entity
        String repositoryBeanName = getRepositoryBeanName(entityClass);

        // Fetch the repository dynamically
        JpaRepository<?, Long> repository =
                (JpaRepository<?, Long>) applicationContext.getBean(repositoryBeanName);

        // Query the existing entity by ID
        return repository
                .findById(entityId)
                .orElseThrow(
                        () ->
                                new EntityNotFoundException(
                                        "Entity of class "
                                                + entityClass.getSimpleName()
                                                + " with ID "
                                                + entityId
                                                + " not found"));
    }

    private String getRepositoryBeanName(Class<?> entityClass) {
        // Get the simple name of the entity class
        String entityName = entityClass.getSimpleName();

        // Remove "DTO" suffix if present
        if (entityName.endsWith("DTO")) {
            entityName = entityName.substring(0, entityName.length() - 3);
        }

        // Convert the entity name to the repository bean name
        // Convention: repositoryBeanName = entityName (lowerCamelCase) + "Repository"
        String repositoryBeanName =
                Character.toLowerCase(entityName.charAt(0))
                        + entityName.substring(1)
                        + "Repository";

        return repositoryBeanName;
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
