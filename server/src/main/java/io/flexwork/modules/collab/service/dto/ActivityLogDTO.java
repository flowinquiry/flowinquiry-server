package io.flexwork.modules.collab.service.dto;

import io.flexwork.modules.collab.domain.EntityType;
import java.time.Instant;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ActivityLogDTO {
    private Long id;
    private EntityType entityType;
    private Long entityId;
    private String content;
    private Instant createdAt;
    private Instant updatedAt;
    private Long createdById;
    private String createdByName;
    private String createdByImageUrl;
}
