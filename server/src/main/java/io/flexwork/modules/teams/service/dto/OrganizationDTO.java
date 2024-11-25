package io.flexwork.modules.teams.service.dto;

import java.util.Set;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrganizationDTO {
    private Long id;
    private String name;
    private String logoUrl;
    private String slogan;
    private String description;
    private Set<TeamDTO> teams;
}
