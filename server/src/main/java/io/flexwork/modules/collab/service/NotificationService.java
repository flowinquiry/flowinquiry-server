package io.flexwork.modules.collab.service;

import io.flexwork.modules.collab.domain.Notification;
import io.flexwork.modules.collab.repository.NotificationRepository;
import io.flexwork.modules.collab.service.dto.NotificationDTO;
import io.flexwork.modules.collab.service.mapper.NotificationMapper;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;

    private final NotificationMapper notificationMapper;

    public NotificationService(
            NotificationRepository notificationRepository, NotificationMapper notificationMapper) {
        this.notificationRepository = notificationRepository;
        this.notificationMapper = notificationMapper;
    }

    @Transactional(readOnly = true)
    public List<NotificationDTO> getUnreadNotificationsForUser(Long userId) {
        return notificationRepository.findByUserIdAndIsReadFalse(userId).stream()
                .map(notificationMapper::toDTO)
                .toList();
    }

    public void markNotificationAsRead(Long notificationId) {
        Notification notification =
                notificationRepository
                        .findById(notificationId)
                        .orElseThrow(() -> new IllegalArgumentException("Notification not found"));
        notification.setRead(true);
        notificationRepository.save(notification);
    }
}
