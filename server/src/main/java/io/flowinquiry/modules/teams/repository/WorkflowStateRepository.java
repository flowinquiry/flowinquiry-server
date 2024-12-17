package io.flowinquiry.modules.teams.repository;

import io.flowinquiry.modules.teams.domain.WorkflowState;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkflowStateRepository extends JpaRepository<WorkflowState, Long> {

    @Query(
            "SELECT ws FROM WorkflowState ws WHERE ws.workflow.id = :workflowId AND ws.isInitial = true")
    WorkflowState findInitialStateByWorkflowId(@Param("workflowId") Long workflowId);

    @Query(
            "SELECT CASE WHEN COUNT(ws) > 0 THEN TRUE ELSE FALSE END "
                    + "FROM WorkflowState ws "
                    + "WHERE ws.workflow.id = :workflowId AND ws.id = :workflowStateId AND ws.isFinal = TRUE")
    boolean isFinalState(
            @Param("workflowId") Long workflowId, @Param("workflowStateId") Long workflowStateId);

    List<WorkflowState> findByWorkflowId(Long workflowId);
}
