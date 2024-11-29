package io.flexwork.modules.audit;

import java.time.Instant;
import lombok.Data;

@Data
public abstract class AbstractAuditDTO {
    Instant createdAt;
    Instant modifiedAt;
}
