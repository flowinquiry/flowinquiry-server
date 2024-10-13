package io.flexwork.modules.fss.domain;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class FsObjectPathId implements Serializable {
    private Long ancestorId;

    private Long descendantId;
}
