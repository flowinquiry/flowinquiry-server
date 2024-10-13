package io.flexwork.modules.usermanagement.domain;

import static io.flexwork.db.DbConstants.MASTER_SCHEMA;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import lombok.*;

@Entity
@Table(name = "fw_tenant", schema = MASTER_SCHEMA)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Tenant extends AbstractAuditingEntity<Long> implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Size(max = 256)
    @NotNull @Column
    private String name;

    @Size(max = 50)
    @NotNull @Column(name = "name_id")
    private String nameId;

    @Size(max = 4000)
    @Column
    private String description;

    @Size(max = 256)
    @Column(length = 256, name = "logo_url")
    private String logoUrl;

    @Size(max = 256)
    @NotNull @Column
    private String domain;
}
