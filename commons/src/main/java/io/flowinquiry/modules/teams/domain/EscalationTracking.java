package io.flowinquiry.modules.teams.domain;

import io.flowinquiry.modules.usermanagement.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "fw_escalation_tracking")
public class EscalationTracking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_request_id", nullable = false)
    private TeamRequest teamRequest;

    @Column(name = "escalation_level", nullable = false)
    private Integer escalationLevel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "escalated_to_user_id")
    private User escalatedToUser;

    @Column(
            name = "escalation_time",
            nullable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime escalationTime;
}
