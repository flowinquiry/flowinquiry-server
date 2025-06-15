package io.flowinquiry.modules.audit.service.listener;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.flowinquiry.modules.audit.service.EntityFieldHandler;
import io.flowinquiry.modules.audit.service.EntityFieldHandlerRegistry;
import io.flowinquiry.modules.audit.service.EntityFieldHandlerRegistryFactory;
import io.flowinquiry.modules.audit.service.event.AuditLogUpdateEvent;
import io.flowinquiry.modules.collab.domain.ActivityLog;
import io.flowinquiry.modules.collab.domain.EntityType;
import io.flowinquiry.modules.collab.repository.ActivityLogRepository;
import io.flowinquiry.modules.usermanagement.domain.User;
import io.flowinquiry.security.SecurityUtils;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;

@ExtendWith(MockitoExtension.class)
public class AuditLogUpdateEventListenerTest {

    @Mock private ActivityLogRepository activityLogRepository;

    @Mock private EntityFieldHandlerRegistryFactory registryFactory;

    @Mock private ApplicationContext applicationContext;

    private AuditLogUpdateEventListener listener;

    @BeforeEach
    public void setup() {
        listener =
                new AuditLogUpdateEventListener(
                        activityLogRepository, registryFactory, applicationContext);
    }

    @Test
    public void testOnNewTicketCreated_WithChanges() {
        // Given
        TestEntity previousEntity = new TestEntity(1L, "Old Name", "Old Description");
        TestEntity updatedEntity = new TestEntity(1L, "New Name", "Old Description");

        AuditLogUpdateEvent event = new AuditLogUpdateEvent(this, previousEntity, updatedEntity);

        // Create a mock registry
        EntityFieldHandlerRegistry registry = mock(EntityFieldHandlerRegistry.class);
        when(registry.getEntityClass()).thenReturn((Class) TestEntity.class);
        when(registry.getEntityType()).thenReturn(EntityType.Ticket);

        // Create handlers for the fields
        Map<String, EntityFieldHandler<TestEntity>> handlers = new HashMap<>();
        handlers.put("name", new EntityFieldHandler<>("Name"));

        // Configure the registry to return handlers
        when(registry.getHandler("name")).thenReturn(handlers.get("name"));

        // Configure the factory to return the registry
        when(registryFactory.getRegistry(TestEntity.class)).thenReturn(registry);

        // Mock the SecurityUtils.getCurrentUserAuditorLogin() method
        try (MockedStatic<SecurityUtils> mockedStatic = Mockito.mockStatic(SecurityUtils.class)) {
            User testUser = User.builder().id(999L).build();
            mockedStatic.when(SecurityUtils::getCurrentUserAuditorLogin).thenReturn(testUser);

            // When
            listener.onNewTicketCreated(event);

            // Then
            ArgumentCaptor<ActivityLog> activityLogCaptor =
                    ArgumentCaptor.forClass(ActivityLog.class);
            verify(activityLogRepository).save(activityLogCaptor.capture());

            ActivityLog capturedLog = activityLogCaptor.getValue();
            assert capturedLog.getEntityId().equals(1L);
            assert capturedLog.getEntityType().equals(EntityType.Ticket);
            assert capturedLog.getCreatedBy() != null;
            assert capturedLog.getCreatedBy().equals(testUser);
            assert capturedLog.getContent().contains("Name");
            assert capturedLog.getContent().contains("Old Name");
            assert capturedLog.getContent().contains("New Name");
        }
    }

    @Test
    public void testOnNewTicketCreated_NoChanges() {
        // Given
        TestEntity previousEntity = new TestEntity(1L, "Same Name", "Same Description");
        TestEntity updatedEntity = new TestEntity(1L, "Same Name", "Same Description");

        AuditLogUpdateEvent event = new AuditLogUpdateEvent(this, previousEntity, updatedEntity);

        // Create a mock registry
        EntityFieldHandlerRegistry registry = mock(EntityFieldHandlerRegistry.class);
        when(registry.getEntityClass()).thenReturn((Class) TestEntity.class);

        // Configure the factory to return the registry
        when(registryFactory.getRegistry(TestEntity.class)).thenReturn(registry);

        // When
        listener.onNewTicketCreated(event);

        // Then - no activity log should be saved
        verify(activityLogRepository, never()).save(any());
    }

    @Test
    public void testOnNewTicketCreated_WithNullValues() {
        // Given
        TestEntity previousEntity = new TestEntity(1L, null, "Old Description");
        TestEntity updatedEntity = new TestEntity(1L, "New Name", null);

        AuditLogUpdateEvent event = new AuditLogUpdateEvent(this, previousEntity, updatedEntity);

        // Create a mock registry
        EntityFieldHandlerRegistry registry = mock(EntityFieldHandlerRegistry.class);
        when(registry.getEntityClass()).thenReturn((Class) TestEntity.class);
        when(registry.getEntityType()).thenReturn(EntityType.Ticket);

        // Create handlers for the fields
        Map<String, EntityFieldHandler<TestEntity>> handlers = new HashMap<>();
        handlers.put("name", new EntityFieldHandler<>("Name"));
        handlers.put("description", new EntityFieldHandler<>("Description"));

        // Configure the registry to return handlers
        when(registry.getHandler("name")).thenReturn(handlers.get("name"));
        when(registry.getHandler("description")).thenReturn(handlers.get("description"));

        // Configure the factory to return the registry
        when(registryFactory.getRegistry(TestEntity.class)).thenReturn(registry);

        // Mock the SecurityUtils.getCurrentUserAuditorLogin() method
        try (MockedStatic<SecurityUtils> mockedStatic = Mockito.mockStatic(SecurityUtils.class)) {
            User testUser = User.builder().id(999L).build();
            mockedStatic.when(SecurityUtils::getCurrentUserAuditorLogin).thenReturn(testUser);

            // When
            listener.onNewTicketCreated(event);

            // Then
            ArgumentCaptor<ActivityLog> activityLogCaptor =
                    ArgumentCaptor.forClass(ActivityLog.class);
            verify(activityLogRepository).save(activityLogCaptor.capture());

            ActivityLog capturedLog = activityLogCaptor.getValue();
            assert capturedLog.getEntityId().equals(1L);
            assert capturedLog.getEntityType().equals(EntityType.Ticket);
            assert capturedLog.getCreatedBy() != null;
            assert capturedLog.getCreatedBy().equals(testUser);
            assert capturedLog.getContent().contains("Name");
            assert capturedLog.getContent().contains("New Name");
            assert capturedLog.getContent().contains("Description");
            assert capturedLog.getContent().contains("Old Description");
        }
    }

    @Test
    public void testOnNewTicketCreated_WithException() {
        // Given
        TestEntity previousEntity = new TestEntity(1L, "Old Name", "Old Description");
        TestEntity updatedEntity = new TestEntity(1L, "New Name", "Old Description");

        AuditLogUpdateEvent event = new AuditLogUpdateEvent(this, previousEntity, updatedEntity);

        // Configure the factory to throw an exception
        when(registryFactory.getRegistry(TestEntity.class))
                .thenThrow(new RuntimeException("Test exception"));

        // When
        listener.onNewTicketCreated(event);

        // Then - no activity log should be saved, but no exception should be thrown
        verify(activityLogRepository, never()).save(any());
    }

    // Test entity class for testing
    private static class TestEntity {
        private Long id;
        private String name;
        private String description;

        public TestEntity(Long id, String name, String description) {
            this.id = id;
            this.name = name;
            this.description = description;
        }

        public Long getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }
    }
}
