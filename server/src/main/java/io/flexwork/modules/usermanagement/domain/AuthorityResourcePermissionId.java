package io.flexwork.modules.usermanagement.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.io.Serializable;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthorityResourcePermissionId implements Serializable {

    @Column(name = "authority_name")
    private String authorityName;

    @Column(name = "resource_name")
    private String resourceName;

    @Column(name = "permission", nullable = false)
    @Enumerated(EnumType.STRING)
    private Permission permission;
}
