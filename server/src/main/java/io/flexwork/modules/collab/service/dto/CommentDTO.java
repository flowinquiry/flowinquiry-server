package io.flexwork.modules.collab.service.dto;

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
    private LocalDateTime createdAt;
    private String entityType;
    private Long entityId;
}
