package io.flexwork.modules.usermanagement.service.dto;

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

    public AuthorityDTO(String name) {
        this(name, name, false);
    }

    public AuthorityDTO(String name, String descriptiveName) {
        this(name, descriptiveName, false);
    }
}
