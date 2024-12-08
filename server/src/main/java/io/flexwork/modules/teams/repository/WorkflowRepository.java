package io.flexwork.modules.teams.repository;

import io.flexwork.modules.teams.domain.Workflow;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkflowRepository
        extends JpaRepository<Workflow, Long>, JpaSpecificationExecutor<Workflow> {

    @Query(
            """
        SELECT w
        FROM Workflow w
        LEFT JOIN TeamWorkflowSelection tws ON w.id = tws.workflow.id AND tws.team.id = :teamId
        WHERE w.owner.id = :teamId
           OR w.visibility = 'PUBLIC'
           OR (w.visibility = 'TEAM' AND tws.id IS NOT NULL)
    """)
    List<Workflow> findAllWorkflowsByTeam(@Param("teamId") Long teamId);

    @Query(
            """
        SELECT CASE
                   WHEN :level = 1 THEN w.level1EscalationTimeout
                   WHEN :level = 2 THEN w.level2EscalationTimeout
                   WHEN :level = 3 THEN w.level3EscalationTimeout
               END
        FROM Workflow w
        WHERE w.id = :workflowId
    """)
    Optional<Integer> findEscalationTimeoutByLevel(
            @Param("workflowId") Long workflowId, @Param("level") int level);

    @EntityGraph(attributePaths = {"states", "transitions", "owner"})
    @Query("SELECT w FROM Workflow w WHERE w.id = :workflowId")
    Optional<Workflow> findWithDetailsById(@Param("workflowId") Long workflowId);
}
