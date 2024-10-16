package io.flexwork.modules.usermanagement.service.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TeamDTO {

    private Long id; // Team ID
    private String name; // Team name
    private String logoUrl; // Team logo URL
    private String slogan; // Team slogan
    private String description; // Team description
    private Long
            organizationId; // Organization ID (to represent the relationship with Organization)
}
