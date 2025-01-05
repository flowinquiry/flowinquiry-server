package io.flowinquiry.modules.teams.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WatcherDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String imageUrl;
    private String email;
}
