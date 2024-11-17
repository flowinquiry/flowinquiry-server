package io.flexwork.modules.usermanagement.service.dto;

import jakarta.validation.constraints.Email;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Set;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
public class UserDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @EqualsAndHashCode.Include private Long id;

    @Email @EqualsAndHashCode.Include private String email;

    private String firstName;

    private String lastName;

    private String timezone;

    private String imageUrl;

    private boolean activated = false;

    private String langKey;

    private LocalDateTime lastLoginTime;

    private Set<AuthorityDTO> authorities;

    private Long managerId;

    private String managerName;

    private String about;

    private String address;

    private String city;

    private String state;

    private String country;

    private String title;

    private String createdBy;

    private Instant createdDate;

    private String lastModifiedBy;

    private Instant lastModifiedDate;
}
