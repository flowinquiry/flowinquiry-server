package io.flowinquiry.modules.teams.service.dto;

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
    private String name;
    private String logoUrl;
    private String slogan;
    private String description;
    private Long organizationId;
    private Long usersCount;
}
