package io.flexwork.modules.collab.service.dto;

import io.flexwork.modules.collab.domain.EntityType;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommentDTO {

    private Long id;
    private String content;
    private Long createdById;
    private String createdByName;
    private String createdByImageUrl;
    private LocalDateTime createdAt;
    private EntityType entityType;
    private Long entityId;
}
