package io.flowinquiry.modules.teams.domain;

import io.flowinquiry.modules.audit.AbstractAuditingEntity;
import io.flowinquiry.modules.usermanagement.domain.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Formula;

@EqualsAndHashCode(callSuper = false)
@Entity
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

    @ManyToMany
    @JoinTable(
            name = "fw_team_request_watchers",
            joinColumns = @JoinColumn(name = "team_request_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> watchers;

    @Formula(
            "(SELECT COUNT(a.id) FROM fw_entity_attachment a WHERE a.entity_type = 'Team_Request' AND a.entity_id = id)")
    private int numberAttachments;

    @OneToOne(
            mappedBy = "teamRequest",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            orphanRemoval = true)
    private TeamRequestConversationHealth conversationHealth;
}
