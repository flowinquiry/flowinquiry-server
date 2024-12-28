package io.flowinquiry.modules.usermanagement.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthorityDTO {

    private String name;

    private String descriptiveName;

    private boolean systemRole;

    private Long usersCount;

    private String description;

    public AuthorityDTO(String name) {
        this(name, name, false, 0L, "");
    }

    public AuthorityDTO(String name, String descriptiveName) {
        this(name, descriptiveName, false, 0L, "");
    }
}
