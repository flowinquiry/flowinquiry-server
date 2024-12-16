package io.flexwork.modules.teams.domain;

import jakarta.persistence.*;
import java.util.Set;
import lombok.*;

@Entity
@Table(name = "fw_organization")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Organization {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(name = "logo_url")
    private String logoUrl;

    private String slogan;

    private String description;

    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "organization")
    private Set<Team> teams;
}
