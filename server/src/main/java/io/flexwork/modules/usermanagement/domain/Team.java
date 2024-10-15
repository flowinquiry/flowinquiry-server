package io.flexwork.modules.usermanagement.domain;

import jakarta.persistence.*;
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

    private String name;

    @Column(name = "logo_url")
    private String logoUrl;

    private String slogan;

    private String description;

    @ManyToOne
    @JoinColumn(name = "organization_id")
    private Organization organization;

    @ManyToMany(mappedBy = "teams")
    private Set<User> members;
}
