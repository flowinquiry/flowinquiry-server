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

    @Convert(converter = PermissionConverter.class)
    @Column(name = "permission", nullable = false)
    private Permission permission;

    @EqualsAndHashCode.Exclude
    @ManyToOne
    @JoinColumn(name = "authority_name", insertable = false, updatable = false)
    private Authority authority;

    @EqualsAndHashCode.Exclude
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
