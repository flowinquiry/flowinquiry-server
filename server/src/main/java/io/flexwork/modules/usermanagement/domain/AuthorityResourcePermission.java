package io.flexwork.modules.usermanagement.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "fw_authority_resource_permission")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthorityResourcePermission {

    @EmbeddedId private AuthorityResourcePermissionId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("authorityName")
    private Authority authority;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId(value = "resourceName")
    private Resource resource;

    @Column(name = "permission", nullable = false, insertable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    private Permission permission;
}
