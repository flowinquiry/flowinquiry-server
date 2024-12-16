package io.flexwork.modules.usermanagement.domain;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "fw_authority_resource_permission")
@IdClass(AuthorityResourcePermissionId.class) // Composite key class
public class AuthorityResourcePermission {
    @EqualsAndHashCode.Include
    @Id
    @Column(name = "authority_name")
    private String authorityName;

    @EqualsAndHashCode.Include
    @Id
    @Column(name = "resource_name")
    private String resourceName;

    @Convert(converter = PermissionConverter.class)
    @Column(name = "permission", nullable = false)
    private Permission permission;

    @ManyToOne
    @JoinColumn(name = "authority_name", insertable = false, updatable = false)
    private Authority authority;

    @ManyToOne
    @JoinColumn(name = "resource_name", insertable = false, updatable = false)
    private Resource resource;

    public AuthorityResourcePermission(
            String authorityName, String resourceName, int permissionCode) {
        this.authorityName = authorityName;
        this.resourceName = resourceName;
        this.permission = Permission.fromCode(permissionCode);
    }
}
