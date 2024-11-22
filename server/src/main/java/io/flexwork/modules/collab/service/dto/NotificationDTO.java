package io.flexwork.modules.collab.service.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotificationDTO {
    private Long id;
    private String content;
    private LocalDateTime createdAt;
    private Long userId;
    private boolean isRead;
}
