package io.flowinquiry.modules.usermanagement.domain;

import io.flowinquiry.modules.audit.AbstractAuditingEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

import static io.flowinquiry.db.DbConstants.MASTER_SCHEMA;

@Entity
@Table(name = "fw_tenant", schema = MASTER_SCHEMA)
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Tenant extends AbstractAuditingEntity<Long> implements Serializable {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
