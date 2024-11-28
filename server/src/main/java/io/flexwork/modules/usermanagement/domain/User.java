package io.flexwork.modules.usermanagement.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.flexwork.modules.audit.AbstractAuditingEntity;
import io.flexwork.modules.teams.domain.Team;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.Set;
import lombok.*;

/** A user. */
@Entity
@Table(name = "fw_user")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User extends AbstractAuditingEntity<Long> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @JsonIgnore
    @NotNull @Size(min = 60, max = 60)
    @Column(name = "password_hash", length = 60, nullable = false)
    @ToString.Exclude
    private String password;

    @Size(max = 50)
    @Column(name = "first_name", length = 50)
    private String firstName;

    @Size(max = 50)
    @Column(name = "last_name", length = 50)
    private String lastName;

    @Email
    @Size(min = 5, max = 254)
    @Column(length = 254, unique = true)
    private String email;

    @NotNull @Column(nullable = false)
    private boolean activated = false;

    @Size(min = 2, max = 10)
    @Column(name = "lang_key", length = 10)
    private String langKey;

    @Size(max = 256)
    @Column(name = "image_url", length = 256)
    private String imageUrl;

    @Size(max = 50)
    @Column(name = "role", length = 50)
    private String role;

    @Size(max = 100)
    @Column(name = "title", length = 100)
    private String title;

    @ManyToOne
    @JoinColumn(name = "manager_id")
    private User manager;

    @Size(max = 20)
    @Column(name = "activation_key", length = 20)
    @JsonIgnore
    private String activationKey;

    @Size(max = 20)
    @Column(name = "reset_key", length = 20)
    @JsonIgnore
    private String resetKey;

    @Column(name = "reset_date")
    private Instant resetDate = null;

    @Column(name = "timezone", nullable = false)
    private String timezone;

    @Column(name = "about", columnDefinition = "TEXT")
    private String about;

    @Column(name = "address", length = 255)
    private String address;

    @Column(name = "city", length = 100)
    private String city;

    @Column(name = "state", length = 100)
    private String state;

    @Column(name = "country", length = 100)
    private String country;

    @Column(name = "last_login_time")
    private LocalDateTime lastLoginTime;

    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @ManyToMany
    @JoinTable(
            name = "fw_user_team",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "team_id"))
    private Set<Team> teams;

    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @ManyToMany
    @JoinTable(
            name = "fw_user_authority",
            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")},
            inverseJoinColumns = {
                @JoinColumn(name = "authority_name", referencedColumnName = "name")
            })
    private Set<Authority> authorities = new HashSet<>();

    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserTeam> userTeams = new HashSet<>();

    public LocalDateTime getLastLoginTime() {
        if (lastLoginTime == null) return null;
        ZoneId userZone =
                (timezone != null) ? ZoneId.of(timezone) : ZoneId.of("America/Los_Angeles");
        return lastLoginTime.atZone(ZoneOffset.UTC).withZoneSameInstant(userZone).toLocalDateTime();
    }
}
