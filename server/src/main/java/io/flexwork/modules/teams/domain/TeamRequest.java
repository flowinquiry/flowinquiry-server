package io.flexwork.modules.teams.domain;

import io.flexwork.modules.collab.domain.Team;
import io.flexwork.modules.usermanagement.domain.User;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "fw_team_request")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamRequest {

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
    private LocalDateTime createdDate;
    private String currentState;

    @PrePersist
    private void prePersist() {
        if (createdDate == null) {
            createdDate = LocalDateTime.now();
        }
    }
}
