package io.flowinquiry.modules.teams.service.dto;

import io.flowinquiry.modules.teams.domain.ProjectStatus;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ProjectDTO {
    private Long id;
    private String name;
    private String description;
    private Long teamId;
    private ProjectStatus status;
    private Instant startDate;
    private Instant endDate;
    private Long createdBy;
    private Instant createdAt;
    private Long modifiedBy;
    private Instant modifiedAt;
}
