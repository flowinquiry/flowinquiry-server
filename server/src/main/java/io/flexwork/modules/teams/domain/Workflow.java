package io.flexwork.modules.teams.domain;

import io.flexwork.modules.audit.AbstractAuditingEntity;
import jakarta.persistence.*;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "fw_workflow")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Workflow extends AbstractAuditingEntity<Long> {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;

    @Column(name = "request_name", length = 255)
    private String requestName;

    @EqualsAndHashCode.Exclude
    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = true)
    private Team owner; // Owner team; null for global workflows

    @Column(nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private WorkflowVisibility visibility;

    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "workflow", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<WorkflowState> states;

    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "workflow", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<WorkflowTransition> transitions;

    @Column(
            name = "level1_escalation_timeout",
            nullable = false,
            columnDefinition = "INT DEFAULT 1000000")
    private Integer level1EscalationTimeout;

    @Column(
            name = "level2_escalation_timeout",
            nullable = false,
            columnDefinition = "INT DEFAULT 1000000")
    private Integer level2EscalationTimeout;

    @Column(
            name = "level3_escalation_timeout",
            nullable = false,
            columnDefinition = "INT DEFAULT 1000000")
    private Integer level3EscalationTimeout;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "parent_workflow_id",
            foreignKey = @ForeignKey(name = "fk_workflow_parent_workflow"))
    private Workflow parentWorkflow; // Reference to the parent workflow

    @Column(name = "cloned_from_global", nullable = false)
    private boolean clonedFromGlobal;

    @Column(columnDefinition = "TEXT")
    private String tags;
}
