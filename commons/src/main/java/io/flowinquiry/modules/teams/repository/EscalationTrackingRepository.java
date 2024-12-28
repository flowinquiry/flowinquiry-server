package io.flowinquiry.modules.teams.repository;

import io.flowinquiry.modules.teams.domain.EscalationTracking;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EscalationTrackingRepository extends JpaRepository<EscalationTracking, Long> {

    /**
     * Finds the maximum escalation level for a specific team request.
     *
     * @param teamRequestId The ID of the team request.
     * @return The highest escalation level for the given request, or empty if no escalations exist.
     */
    @Query(
            """
        SELECT MAX(et.escalationLevel)
        FROM EscalationTracking et
        WHERE et.teamRequest.id = :teamRequestId
    """)
    Optional<Integer> findMaxEscalationLevel(@Param("teamRequestId") Long teamRequestId);

    /**
     * Finds all escalations that occurred before a specific threshold for a given escalation level.
     *
     * @param teamRequestId The ID of the team request.
     * @param escalationLevel The escalation level to filter by.
     * @param threshold The timestamp threshold for escalation time.
     * @return True if any escalations exist for the given level before the threshold, otherwise
     *     false.
     */
    @Query(
            """
        SELECT COUNT(et) > 0
        FROM EscalationTracking et
        WHERE et.teamRequest.id = :teamRequestId
          AND et.escalationLevel = :escalationLevel
          AND et.escalationTime < :threshold
    """)
    boolean existsEscalationBeforeThreshold(
            @Param("teamRequestId") Long teamRequestId,
            @Param("escalationLevel") int escalationLevel,
            @Param("threshold") LocalDateTime threshold);
}
