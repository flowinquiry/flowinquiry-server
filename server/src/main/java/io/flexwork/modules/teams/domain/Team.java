package io.flexwork.modules.teams.domain;

import io.flexwork.modules.usermanagement.domain.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;
import lombok.*;
import org.hibernate.annotations.Formula;

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

    @EqualsAndHashCode.Exclude
    @ManyToMany
    @JoinTable(
            name = "fw_user_team",
            joinColumns = @JoinColumn(name = "team_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"))
    private Set<User> users;

    @Formula("(SELECT COUNT(ut.user_id) FROM fw_user_team ut WHERE ut.team_id = id)")
    private Long usersCount;

    @EqualsAndHashCode.Exclude
    @Builder.Default
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Workflow> workflows = new HashSet<>();

    @EqualsAndHashCode.Exclude
    @Builder.Default
    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<TeamWorkflowSelection> workflowUsages = new HashSet<>();
}
