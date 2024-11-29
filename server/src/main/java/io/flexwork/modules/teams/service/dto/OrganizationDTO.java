package io.flexwork.modules.teams.service.dto;

import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class OrganizationDTO {
    private Long id;
    private String name;
    private String logoUrl;
    private String slogan;
    private String description;
    private Set<TeamDTO> teams;
}
