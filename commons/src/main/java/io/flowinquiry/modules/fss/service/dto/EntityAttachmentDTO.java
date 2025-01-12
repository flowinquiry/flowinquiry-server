package io.flowinquiry.modules.fss.service.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class EntityAttachmentDTO {
    private Long id;
    private String entityType;
    private Long entityId;
    private String fileName;
    private String fileType;
    private Long fileSize;
    private String fileUrl;
    private LocalDateTime uploadedAt;
}
