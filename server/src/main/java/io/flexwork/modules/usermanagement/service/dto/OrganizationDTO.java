package io.flexwork.modules.usermanagement.service.dto;

import java.util.Set;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrganizationDTO {
    private Long id; // Organization ID
    private String name; // Organization name
    private String logoUrl; // Logo URL
    private String slogan; // Organization slogan
    private String description; // Description of the organization
    private Set<TeamDTO> teams; // Set of team IDs
}
