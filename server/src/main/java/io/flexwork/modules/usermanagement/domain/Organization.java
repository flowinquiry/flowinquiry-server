package io.flexwork.modules.usermanagement.domain;

import jakarta.persistence.*;
import java.util.Set;
import lombok.*;

@Entity
@Table(name = "fw_organization")
@Data
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

    @OneToMany(mappedBy = "organization")
    private Set<Team> teams;

    @OneToMany(mappedBy = "organization")
    private Set<User> employees;
}
