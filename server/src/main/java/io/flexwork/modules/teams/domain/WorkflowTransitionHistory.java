package io.flexwork.modules.teams.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "fw_workflow_transition_history")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowTransitionHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "team_request_id", nullable = false)
    private TeamRequest teamRequest;

    @ManyToOne
    @JoinColumn(name = "from_state_id", nullable = false)
    private WorkflowState fromState;

    @ManyToOne
    @JoinColumn(name = "to_state_id", nullable = false)
    private WorkflowState toState;

    @Column(name = "event_name", nullable = false)
    private String eventName;

    @Column(name = "transition_date", nullable = false)
    private ZonedDateTime transitionDate;

    @Column(name = "sla_due_date")
    private ZonedDateTime slaDueDate;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private WorkflowTransitionHistoryStatus status;
}
