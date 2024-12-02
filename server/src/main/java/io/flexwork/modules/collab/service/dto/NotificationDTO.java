package io.flexwork.modules.collab.service.dto;

import java.time.Instant;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotificationDTO {
    private Long id;
    private String content;
    private Long userId;
    private Boolean isRead;
    private Instant createdAt;
}
