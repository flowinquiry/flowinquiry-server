package io.flexwork.modules.usermanagement.domain;

import io.flexwork.modules.teams.domain.Team;
import io.flexwork.modules.teams.domain.TeamRole;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "fw_user_team")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserTeam {
    @EmbeddedId private UserTeamId id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @MapsId("teamId")
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @ManyToOne
    @JoinColumn(
            name = "role_name",
            referencedColumnName = "name",
            nullable = false,
            insertable = false,
            updatable = false)
    private TeamRole role;
}
