package io.flexwork.modules.collab.service;

import io.flexwork.modules.collab.repository.NotificationRepository;
import io.flexwork.modules.collab.service.dto.NotificationDTO;
import io.flexwork.modules.collab.service.mapper.NotificationMapper;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
        return notificationRepository
                .findByUserIdAndIsReadFalse(userId, Sort.by(Sort.Direction.DESC, "createdAt"))
                .stream()
                .map(notificationMapper::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<NotificationDTO> getNotificationsForUser(Long userId, Pageable pageable) {
        return notificationRepository.findByUserId(userId, pageable).map(notificationMapper::toDTO);
    }

    @Transactional
    public void markNotificationsAsRead(List<Long> notificationIds) {
        notificationRepository.markAsRead(notificationIds);
    }
}
