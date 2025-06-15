package io.flowinquiry.modules.teams.service.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class TeamDTO {
    private Long id;

    @Size(max = 255) private String name;

    @Size(max = 500) private String logoUrl;

    @Size(max = 255) private String slogan;

    private String description;
    private Long organizationId;
    private Long usersCount;
}
