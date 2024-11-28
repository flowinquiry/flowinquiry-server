package io.flexwork.modules.fss.domain;

import io.flexwork.modules.audit.AbstractAuditingEntity;
import jakarta.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import lombok.*;

@Entity
@Table(name = "fw_fss_object")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FsObject extends AbstractAuditingEntity<Long> implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "name", length = 256, unique = true)
    private String name;

    @Column(name = "description", length = 4000)
    private String description;

    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "descendant", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<FsObjectPath> ancestors = new HashSet<>();

    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "ancestor", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<FsObjectPath> descendants = new HashSet<>();
}
