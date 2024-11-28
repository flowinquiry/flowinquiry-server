package io.flexwork.modules.teams.repository;

import io.flexwork.modules.teams.domain.TeamRequest;
import io.flexwork.modules.teams.service.dto.SlaDurationDTO;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamRequestRepository
        extends JpaRepository<TeamRequest, Long>, JpaSpecificationExecutor<TeamRequest> {

    @EntityGraph(attributePaths = {"team", "requestUser", "assignUser", "workflow"})
    Page<TeamRequest> findAll(Specification<TeamRequest> spec, Pageable pageable);

    @EntityGraph(attributePaths = {"team", "requestUser", "assignUser", "workflow"})
    Optional<TeamRequest> findById(@Param("id") Long id);

    @EntityGraph(attributePaths = {"team", "requestUser", "assignUser", "workflow"})
    @Query(
            value =
                    """
        SELECT tr
        FROM TeamRequest tr
        WHERE tr.team.id = (
            SELECT r.team.id
            FROM TeamRequest r
            WHERE r.id = :requestId
        )
        AND tr.id < :requestId
        ORDER BY tr.id DESC
            LIMIT 1
    """)
    Optional<TeamRequest> findPreviousEntity(@Param("requestId") Long requestId);

    @EntityGraph(attributePaths = {"team", "requestUser", "assignUser", "workflow"})
    @Query(
            value =
                    """
        SELECT tr
        FROM TeamRequest tr
        WHERE tr.team.id = (
            SELECT r.team.id
            FROM TeamRequest r
            WHERE r.id = :requestId
        )
        AND tr.id > :requestId
        ORDER BY tr.id ASC
            LIMIT 1
    """)
    Optional<TeamRequest> findNextEntity(@Param("requestId") Long requestId);

    @Query(
            """
        SELECT new io.flexwork.modules.teams.service.dto.SlaDurationDTO(
            tr.sourceState,
            tr.targetState,
            tr.slaDuration,
            tr.eventName
        )
        FROM TeamRequest r
        JOIN WorkflowTransition tr
            ON r.workflow.id = tr.workflow.id
            AND r.currentState = tr.sourceState
        WHERE r.id = :teamRequestId
    """)
    List<SlaDurationDTO> findSlaDurationsForCurrentState(
            @Param("teamRequestId") Long teamRequestId);

    /**
     * Finds all distinct workflow IDs associated with team requests.
     *
     * @return A list of workflow IDs.
     */
    @Query("SELECT DISTINCT r.workflow.id FROM TeamRequest r")
    List<Long> findAllWorkflowIds();
}
