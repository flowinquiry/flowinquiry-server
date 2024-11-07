package io.flexwork.modules.teams.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "fw_workflow_transition_history")
public class WorkflowTransitionHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "team_request_id", nullable = false)
    private TeamRequest teamRequest;

    private String fromState;
    private String toState;
    private String eventName;
    private LocalDateTime transitionDate;
    private LocalDateTime slaDueDate;
    private String status;
}
