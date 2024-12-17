package io.flowinquiry.modules.usermanagement.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthorityResourcePermissionDTO {

    private String authorityName;
    private String resourceName;
    private String permission;
}
