package io.flowinquiry.modules.teams.repository;

import io.flowinquiry.modules.teams.domain.TeamRequestConversationHealth;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamRequestConversationHealthRepository
        extends JpaRepository<TeamRequestConversationHealth, Long> {

    Optional<TeamRequestConversationHealth> findByTeamRequestId(Long teamRequestId);
}
