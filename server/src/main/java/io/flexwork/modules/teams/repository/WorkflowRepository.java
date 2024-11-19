package io.flexwork.modules.teams.repository;

import io.flexwork.modules.teams.domain.Workflow;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkflowRepository extends JpaRepository<Workflow, Long> {

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
}
