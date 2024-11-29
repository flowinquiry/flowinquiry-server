package io.flexwork.modules.collab.service.dto;

import io.flexwork.modules.audit.AbstractAuditDTO;
import io.flexwork.modules.collab.domain.EntityType;
import java.time.Instant;
import lombok.Data;

@Data
public class CommentDTO extends AbstractAuditDTO {

    private Long id;
    private String content;
    private Long createdById;
    private String createdByName;
    private String createdByImageUrl;
    private Instant createdAt;
    private EntityType entityType;
    private Long entityId;
}
