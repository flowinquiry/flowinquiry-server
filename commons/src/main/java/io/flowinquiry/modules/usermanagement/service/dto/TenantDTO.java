package io.flowinquiry.modules.usermanagement.service.dto;

import java.io.Serializable;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TenantDTO implements Serializable {
    private Long id;

    private String name;

    private String description;

    private String logoUrl;

    private String domain;
}
