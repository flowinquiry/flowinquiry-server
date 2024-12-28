package io.flowinquiry.modules.usermanagement.service.dto;

import jakarta.validation.constraints.Email;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
public class UserWithTeamRoleDTO implements Serializable {

    @EqualsAndHashCode.Include private Long id;

    @Email @EqualsAndHashCode.Include private String email;

    private String firstName;

    private String lastName;

    private String timezone;

    private String imageUrl;

    private String title;

    private Long teamId;

    private String teamRole;

    public UserWithTeamRoleDTO(
            Long id,
            String email,
            String firstName,
            String lastName,
            String timezone,
            String imageUrl,
            String title,
            Long teamId,
            String teamRole) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.timezone = timezone;
        this.imageUrl = imageUrl;
        this.title = title;
        this.teamId = teamId;
        this.teamRole = teamRole;
    }
}
