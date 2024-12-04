package io.flexwork.modules.usermanagement.domain;

import jakarta.persistence.Column;
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
}
