package io.flowinquiry.modules.collab.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppSettingDTO {
    private String key;
    private String value;
    private String type;
    private String group;
    private String description;
}
