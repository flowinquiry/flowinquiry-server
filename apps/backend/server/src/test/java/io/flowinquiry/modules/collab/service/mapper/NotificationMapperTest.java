package io.flowinquiry.modules.collab.service.mapper;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import io.flowinquiry.modules.collab.domain.Notification;
import io.flowinquiry.modules.collab.domain.NotificationType;
import io.flowinquiry.modules.collab.service.dto.NotificationDTO;
import io.flowinquiry.modules.usermanagement.domain.User;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

public class NotificationMapperTest {

    private NotificationMapper notificationMapper = Mappers.getMapper(NotificationMapper.class);

    @Test
    public void testToDTO() {
        // Given
        Instant now = Instant.now();
        User user = User.builder().id(1L).firstName("John").lastName("Doe").build();

        Notification notification =
                Notification.builder()
                        .id(1L)
                        .content("Test notification")
                        .type(NotificationType.INFO)
                        .user(user)
                        .isRead(false)
                        .build();
        notification.setCreatedAt(now);

        // When
        NotificationDTO notificationDTO = notificationMapper.toDTO(notification);

        // Then
        assertAll(
                () -> assertEquals(notification.getId(), notificationDTO.getId()),
                () -> assertEquals(notification.getContent(), notificationDTO.getContent()),
                () -> assertEquals(notification.getType(), notificationDTO.getType()),
                () -> assertEquals(notification.getUser().getId(), notificationDTO.getUserId()),
                () -> assertEquals(notification.getIsRead(), notificationDTO.getIsRead()),
                () -> assertEquals(notification.getCreatedAt(), notificationDTO.getCreatedAt()));
    }

    @Test
    public void testToDTOWithNullUser() {
        // Given
        Instant now = Instant.now();
        Notification notification =
                Notification.builder()
                        .id(1L)
                        .content("Test notification")
                        .type(NotificationType.INFO)
                        .user(null)
                        .isRead(false)
                        .build();
        notification.setCreatedAt(now);

        // When
        NotificationDTO notificationDTO = notificationMapper.toDTO(notification);

        // Then
        assertAll(
                () -> assertEquals(notification.getId(), notificationDTO.getId()),
                () -> assertEquals(notification.getContent(), notificationDTO.getContent()),
                () -> assertEquals(notification.getType(), notificationDTO.getType()),
                () -> assertNull(notificationDTO.getUserId()),
                () -> assertEquals(notification.getIsRead(), notificationDTO.getIsRead()),
                () -> assertEquals(notification.getCreatedAt(), notificationDTO.getCreatedAt()));
    }

    @Test
    public void testToEntity() {
        // Given
        Instant now = Instant.now();
        NotificationDTO notificationDTO =
                NotificationDTO.builder()
                        .id(1L)
                        .content("Test notification")
                        .type(NotificationType.INFO)
                        .userId(1L)
                        .isRead(false)
                        .createdAt(now)
                        .build();

        // When
        Notification notification = notificationMapper.toEntity(notificationDTO);

        // Then
        assertAll(
                () -> assertEquals(notificationDTO.getId(), notification.getId()),
                () -> assertEquals(notificationDTO.getContent(), notification.getContent()),
                () -> assertEquals(notificationDTO.getType(), notification.getType()),
                () -> assertEquals(notificationDTO.getUserId(), notification.getUser().getId()),
                () -> assertEquals(notificationDTO.getIsRead(), notification.getIsRead()));
    }
}
