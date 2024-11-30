package io.flexwork.modules.teams.repository;

import io.flexwork.modules.teams.domain.WorkflowTransitionHistory;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkflowTransitionHistoryRepository
        extends JpaRepository<WorkflowTransitionHistory, Long> {

    /**
     * Finds and sorts the workflow transition history for a specific team request.
     *
     * @param teamRequestId the ID of the team request
     * @return a list of WorkflowTransitionHistory sorted by transitionDate
     */
    @Query(
            "SELECT wth FROM WorkflowTransitionHistory wth "
                    + "WHERE wth.teamRequest.id = :teamRequestId "
                    + "ORDER BY wth.transitionDate ASC")
    List<WorkflowTransitionHistory> findByTeamRequestId(@Param("teamRequestId") Long teamRequestId);
}
