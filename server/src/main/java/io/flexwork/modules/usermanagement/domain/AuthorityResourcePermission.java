package io.flexwork.modules.usermanagement.domain;

import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "fw_authority_resource_permission")
@IdClass(AuthorityResourcePermissionId.class) // Composite key class
public class AuthorityResourcePermission {
    @Id
    @Column(name = "authority_name")
    private String authorityName;

    @Id
    @Column(name = "resource_name")
    private String resourceName;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "permission", nullable = false)
    @Id
    private Permission permission;

    @ManyToOne
    @JoinColumn(name = "authority_name", insertable = false, updatable = false)
    private Authority authority;

    @ManyToOne
    @JoinColumn(name = "resource_name", insertable = false, updatable = false)
    private Resource resource;

    public AuthorityResourcePermission(
            String authorityName, String resourceName, Permission permission) {
        this.authorityName = authorityName;
        this.resourceName = resourceName;
        this.permission = permission;
    }
}
