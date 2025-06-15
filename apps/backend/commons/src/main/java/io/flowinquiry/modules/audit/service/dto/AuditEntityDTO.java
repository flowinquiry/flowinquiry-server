package io.flowinquiry.modules.audit.service.dto;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class AuditEntityDTO {
    private Long createdBy;
    private String createdByUserName; // Fetching username instead of entity reference
    private Instant createdAt;
    private Long modifiedBy;
    private String modifiedByUserName; // Fetching username instead of entity reference
    private Instant modifiedAt;
}
