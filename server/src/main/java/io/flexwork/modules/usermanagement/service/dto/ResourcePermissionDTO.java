package io.flexwork.modules.usermanagement.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResourcePermissionDTO {
    private String resourceName;
    private String permission;
}
