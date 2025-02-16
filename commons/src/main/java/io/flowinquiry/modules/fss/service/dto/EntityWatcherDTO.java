package io.flowinquiry.modules.fss.service.dto;

import java.time.Instant;
import lombok.Data;

@Data
public class EntityWatcherDTO {
    private Long id;
    private String entityType;
    private Long entityId;
    private Long watchUserId;
    private String watchUserName;
    private Instant createdAt;
    private Long createdBy;
}
