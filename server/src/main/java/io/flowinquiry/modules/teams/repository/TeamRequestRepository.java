package io.flowinquiry.modules.teams.repository;

import io.flowinquiry.modules.teams.domain.TeamRequest;
import io.flowinquiry.modules.teams.domain.WorkflowTransitionHistoryStatus;
import io.flowinquiry.modules.teams.service.dto.PriorityDistributionDTO;
import io.flowinquiry.modules.teams.service.dto.TeamTicketPriorityDistributionDTO;
import io.flowinquiry.modules.teams.service.dto.TicketActionCountByDateDTO;
import io.flowinquiry.modules.teams.service.dto.TicketDistributionDTO;
import io.flowinquiry.modules.usermanagement.service.dto.TicketStatisticsDTO;
import java.time.Instant;
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

    @EntityGraph(
            attributePaths = {
                "team",
                "requestUser",
                "assignUser",
                "modifiedByUser",
                "workflow",
                "currentState"
            })
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
            "SELECT new io.flowinquiry.modules.teams.service.dto.TicketDistributionDTO("
                    + "u.id, CONCAT(u.firstName, ' ', u.lastName), COUNT(r.id)) "
                    + "FROM TeamRequest r "
                    + "LEFT JOIN User u ON r.assignUser.id = u.id "
                    + "WHERE r.team.id = :teamId AND r.isCompleted = false AND r.isDeleted = false "
                    + "GROUP BY u.id, u.firstName, u.lastName")
    List<TicketDistributionDTO> findTicketDistributionByTeamId(@Param("teamId") Long teamId);

    @Query(
            "SELECT r FROM TeamRequest r "
                    + "WHERE r.team.id = :teamId AND r.isCompleted = false AND r.isDeleted = false "
                    + "AND r.assignUser IS NULL")
    Page<TeamRequest> findUnassignedTicketsByTeamId(
            @Param("teamId") Long teamId, Pageable pageable);

    // Query to count tickets by priority for a specific team
    @Query(
            "SELECT new io.flowinquiry.modules.teams.service.dto.PriorityDistributionDTO("
                    + "r.priority, COUNT(r.id)) "
                    + "FROM TeamRequest r "
                    + "WHERE r.team.id = :teamId AND r.isCompleted = false AND r.isDeleted = false "
                    + "GROUP BY r.priority")
    List<PriorityDistributionDTO> findTicketPriorityDistributionByTeamId(
            @Param("teamId") Long teamId);

    @Query(
            "SELECT new io.flowinquiry.modules.usermanagement.service.dto.TicketStatisticsDTO("
                    + "COUNT(tr), "
                    + "SUM(CASE WHEN tr.isCompleted = false THEN 1 ELSE 0 END), "
                    + "SUM(CASE WHEN tr.isCompleted = true THEN 1 ELSE 0 END)) "
                    + "FROM TeamRequest tr "
                    + "WHERE tr.isDeleted = false AND tr.team.id = :teamId")
    TicketStatisticsDTO getTicketStatisticsByTeamId(@Param("teamId") Long teamId);

    @Query(
            "SELECT r "
                    + "FROM TeamRequest r "
                    + "JOIN WorkflowTransitionHistory h ON h.teamRequest.id = r.id "
                    + "WHERE r.isDeleted = false "
                    + "AND r.isCompleted = false "
                    + "AND h.slaDueDate IS NOT NULL "
                    + "AND h.slaDueDate < CURRENT_TIMESTAMP "
                    + "AND h.status <> :status "
                    + "AND r.team.id = :teamId")
    Page<TeamRequest> findOverdueTicketsByTeamId(
            @Param("teamId") Long teamId,
            @Param("status") WorkflowTransitionHistoryStatus completedStatus,
            Pageable pageable);

    @Query(
            "SELECT r "
                    + "FROM TeamRequest r "
                    + "JOIN WorkflowTransitionHistory h ON h.teamRequest.id = r.id "
                    + "JOIN UserTeam ut ON ut.team.id = r.team.id "
                    + "WHERE r.isDeleted = false "
                    + "AND r.isCompleted = false "
                    + "AND h.slaDueDate IS NOT NULL "
                    + "AND h.slaDueDate < CURRENT_TIMESTAMP "
                    + "AND h.status <> :status "
                    + "AND ut.user.id = :userId")
    Page<TeamRequest> findOverdueTicketsByUserId(
            @Param("userId") Long userId,
            @Param("status") WorkflowTransitionHistoryStatus completedStatus,
            Pageable pageable);

    @Query(
            "SELECT COUNT(r.id) "
                    + "FROM TeamRequest r "
                    + "JOIN WorkflowTransitionHistory h ON h.teamRequest.id = r.id "
                    + "WHERE r.isDeleted = false "
                    + "AND r.isCompleted = false "
                    + "AND h.slaDueDate IS NOT NULL "
                    + "AND h.slaDueDate < CURRENT_TIMESTAMP "
                    + "AND h.status <> :status "
                    + "AND r.team.id = :teamId")
    Long countOverdueTicketsByTeamId(
            @Param("teamId") Long teamId,
            @Param("status") WorkflowTransitionHistoryStatus completedStatus);

    @Query(
            "SELECT new io.flowinquiry.modules.teams.service.dto.TicketActionCountByDateDTO("
                    + "CAST(r.createdAt AS date), "
                    + "COUNT(r.id), "
                    + "COALESCE(closedTicketCounts.closedCount, 0)) "
                    + "FROM TeamRequest r "
                    + "LEFT JOIN ("
                    + "    SELECT CAST(c.actualCompletionDate AS date) AS completionDate, COUNT(c.id) AS closedCount "
                    + "    FROM TeamRequest c "
                    + "    WHERE c.isDeleted = false "
                    + "    AND c.team.id = :teamId "
                    + "    AND c.actualCompletionDate IS NOT NULL "
                    + "    GROUP BY CAST(c.actualCompletionDate AS date)"
                    + ") closedTicketCounts ON CAST(r.createdAt AS date) = closedTicketCounts.completionDate "
                    + "WHERE r.isDeleted = false "
                    + "AND r.team.id = :teamId "
                    + "AND r.createdAt >= :startDate "
                    + "GROUP BY CAST(r.createdAt AS date), closedTicketCounts.closedCount "
                    + "ORDER BY CAST(r.createdAt AS date) ASC")
    List<TicketActionCountByDateDTO> findTicketActionByDaySeries(
            @Param("teamId") Long teamId, @Param("startDate") Instant startDate);

    @Query(
            """
        SELECT new io.flowinquiry.modules.teams.service.dto.TeamTicketPriorityDistributionDTO(
            r.team.id,
            r.team.name,
            r.priority,
            COUNT(r.id)
        )
        FROM TeamRequest r
        JOIN UserTeam ut ON ut.team.id = r.team.id
        WHERE ut.user.id = :userId
        AND r.isDeleted = false
        AND r.isCompleted = false
        GROUP BY r.team.id, r.team.name, r.priority
    """)
    List<TeamTicketPriorityDistributionDTO> findPriorityDistributionByUserId(
            @Param("userId") Long userId);
}
