package io.flowinquiry.modules.teams.domain;

import io.flowinquiry.utils.JsonbConverter;
import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "fw_project_setting")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "project_id",
            nullable = false,
            unique = true,
            foreignKey = @ForeignKey(name = "fk_project_setting_project"))
    private Project project;

    @Column(name = "sprint_length_days", nullable = false)
    private Integer sprintLengthDays;

    @Column(name = "default_priority", nullable = false)
    private Integer defaultPriority;

    @Column(name = "estimation_unit")
    @Enumerated(EnumType.STRING)
    private EstimationUnit estimationUnit;

    @Column(name = "enable_estimation", nullable = false)
    private boolean enableEstimation = true;

    @Column(name = "integration_settings", columnDefinition = "jsonb")
    @Convert(converter = JsonbConverter.class)
    private Map<String, Object> integrationSettings;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "modified_by")
    private Long modifiedBy;

    @Column(name = "modified_at", nullable = false)
    private OffsetDateTime modifiedAt;
}
