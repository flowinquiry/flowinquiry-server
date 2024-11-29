package io.flexwork.modules.teams.repository;

import io.flexwork.modules.teams.domain.TeamRequest;
import io.flexwork.modules.teams.service.dto.PriorityDistributionDTO;
import io.flexwork.modules.teams.service.dto.TicketDistributionDTO;
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

    @EntityGraph(attributePaths = {"team", "requestUser", "assignUser", "workflow", "currentState"})
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

    /**
     * Finds all distinct workflow IDs associated with team requests.
     *
     * @return A list of workflow IDs.
     */
    @Query("SELECT DISTINCT r.workflow.id FROM TeamRequest r")
    List<Long> findAllWorkflowIds();

    // Query to count tickets assigned to each team member for a specific team
    @Query(
            "SELECT new io.flexwork.modules.teams.service.dto.TicketDistributionDTO("
                    + "u.id, CONCAT(u.firstName, ' ', u.lastName), COUNT(r.id)) "
                    + "FROM TeamRequest r "
                    + "LEFT JOIN User u ON r.assignUser.id = u.id "
                    + "WHERE r.team.id = :teamId AND r.isCompleted = false AND r.isDeleted = false "
                    + "GROUP BY u.id, u.firstName, u.lastName")
    List<TicketDistributionDTO> findTicketDistributionByTeamId(@Param("teamId") Long teamId);

    @Query(
            "SELECT r FROM TeamRequest r "
                    + "WHERE r.team.id = :teamId AND r.isCompleted = false AND r.isDeleted = false "
                    + "AND r.assignUser IS NULL "
                    + "ORDER BY CASE r.priority "
                    + "  WHEN io.flexwork.modules.teams.domain.TeamRequestPriority.Trivial THEN 1 "
                    + "  WHEN io.flexwork.modules.teams.domain.TeamRequestPriority.Low THEN 2 "
                    + "  WHEN io.flexwork.modules.teams.domain.TeamRequestPriority.Medium THEN 3 "
                    + "  WHEN io.flexwork.modules.teams.domain.TeamRequestPriority.High THEN 4 "
                    + "  WHEN io.flexwork.modules.teams.domain.TeamRequestPriority.Critical THEN 5 "
                    + "END ASC")
    Page<TeamRequest> findUnassignedTicketsByTeamIdAsc(
            @Param("teamId") Long teamId, Pageable pageable);

    @Query(
            "SELECT r FROM TeamRequest r "
                    + "WHERE r.team.id = :teamId AND r.isCompleted = false AND r.isDeleted = false "
                    + "AND r.assignUser IS NULL "
                    + "ORDER BY CASE r.priority "
                    + "  WHEN io.flexwork.modules.teams.domain.TeamRequestPriority.Trivial THEN 1 "
                    + "  WHEN io.flexwork.modules.teams.domain.TeamRequestPriority.Low THEN 2 "
                    + "  WHEN io.flexwork.modules.teams.domain.TeamRequestPriority.Medium THEN 3 "
                    + "  WHEN io.flexwork.modules.teams.domain.TeamRequestPriority.High THEN 4 "
                    + "  WHEN io.flexwork.modules.teams.domain.TeamRequestPriority.Critical THEN 5 "
                    + "END DESC")
    Page<TeamRequest> findUnassignedTicketsByTeamIdDesc(
            @Param("teamId") Long teamId, Pageable pageable);

    // Query to count tickets by priority for a specific team
    @Query(
            "SELECT new io.flexwork.modules.teams.service.dto.PriorityDistributionDTO("
                    + "r.priority, COUNT(r.id)) "
                    + "FROM TeamRequest r "
                    + "WHERE r.team.id = :teamId AND r.isCompleted = false AND r.isDeleted = false "
                    + "GROUP BY r.priority")
    List<PriorityDistributionDTO> findTicketPriorityDistributionByTeamId(
            @Param("teamId") Long teamId);
}
