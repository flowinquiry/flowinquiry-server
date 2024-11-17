package io.flexwork.modules.usermanagement.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;
import lombok.*;

@Entity
@Table(name = "fw_team")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(max = 255)
    @Column(name = "name")
    private String name;

    @Size(max = 500)
    @Column(name = "logo_url")
    private String logoUrl;

    @Size(max = 255)
    @Column(name = "slogan")
    private String slogan;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @ManyToOne
    @EqualsAndHashCode.Exclude
    @JoinColumn(name = "organization_id")
    private Organization organization;

    @ManyToMany(mappedBy = "teams")
    @EqualsAndHashCode.Exclude
    private Set<User> users = new HashSet<>();

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
    @EqualsAndHashCode.Exclude
    private Set<UserTeam> teamMembers = new HashSet<>();
}
