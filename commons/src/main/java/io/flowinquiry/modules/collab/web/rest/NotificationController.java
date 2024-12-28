package io.flowinquiry.modules.collab.web.rest;

import io.flowinquiry.modules.collab.service.NotificationService;
import io.flowinquiry.modules.collab.service.dto.NotificationDTO;
import java.util.List;
import lombok.Data;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<NotificationDTO>> getUserNotifications(
            @PathVariable("userId") Long userId, Pageable pageable) {
        return ResponseEntity.ok(notificationService.getNotificationsForUser(userId, pageable));
    }

    @PostMapping("/mark-read")
    public ResponseEntity<Void> markNotificationsAsRead(@RequestBody MarkReadRequest request) {
        if (request.getNotificationIds() == null || request.getNotificationIds().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        notificationService.markNotificationsAsRead(request.getNotificationIds());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/unread")
    public ResponseEntity<List<NotificationDTO>> getUnreadNotifications(
            @RequestParam("userId") Long userId) {
        List<NotificationDTO> notifications =
                notificationService.getUnreadNotificationsForUser(userId);
        return ResponseEntity.ok(notifications);
    }

    @Data
    public static class MarkReadRequest {
        private List<Long> notificationIds;
    }
}
