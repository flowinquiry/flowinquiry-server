package io.flowinquiry.modules.teams.domain;

import io.flowinquiry.modules.audit.AbstractAuditingEntity;
import io.flowinquiry.modules.usermanagement.domain.User;
import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Formula;
import org.springframework.cache.annotation.Cacheable;

@EqualsAndHashCode(callSuper = false)
@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(name = "fw_team_request")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamRequest extends AbstractAuditingEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @ManyToOne
    @JoinColumn(name = "workflow_id", nullable = false)
    private Workflow workflow;

    @ManyToOne
    @JoinColumn(name = "request_user_id", nullable = false)
    private User requestUser;

    @ManyToOne
    @JoinColumn(name = "assign_user_id")
    private User assignUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", foreignKey = @ForeignKey(name = "fk_team_request_project"))
    private Project project;

    private String requestTitle;

    private String requestDescription;

    @Column(nullable = false)
    @Convert(converter = TeamRequestPriorityConverter.class)
    private TeamRequestPriority priority;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    @Column(name = "is_recurring", nullable = false)
    private boolean isRecurring = false;

    @Column(name = "estimated_completion_date")
    private LocalDate estimatedCompletionDate;

    @Column(name = "actual_completion_date")
    private LocalDate actualCompletionDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_state_id")
    private WorkflowState currentState;

    @Column(name = "channel")
    @Convert(converter = TicketChannelConverter.class)
    private TicketChannel channel;

    @Column(name = "is_new", nullable = false)
    private Boolean isNew = true;

    @Column(name = "is_completed", nullable = false)
    private Boolean isCompleted = false;

    @Formula(
            "(SELECT COUNT(a.id) FROM fw_entity_attachment a WHERE a.entity_type = 'Team_Request' AND a.entity_id = id)")
    private int numberAttachments;

    @Formula(
            "(SELECT COUNT(a.id) FROM fw_entity_watchers a WHERE a.entity_type = 'Team_Request' AND a.entity_id = id)")
    private int numberWatchers;

    @OneToOne(
            mappedBy = "teamRequest",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            orphanRemoval = true)
    private TeamRequestConversationHealth conversationHealth;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "iteration_id", foreignKey = @ForeignKey(name = "fk_team_request_iteration"))
    private ProjectIteration iteration;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "epic_id", foreignKey = @ForeignKey(name = "fk_team_request_epic"))
    private ProjectEpic epic;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TShirtSize size;

    @Column(nullable = false)
    private Integer estimate;
}
