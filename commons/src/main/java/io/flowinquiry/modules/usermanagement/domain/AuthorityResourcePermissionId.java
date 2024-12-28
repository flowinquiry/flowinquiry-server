package io.flowinquiry.modules.usermanagement.domain;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

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
