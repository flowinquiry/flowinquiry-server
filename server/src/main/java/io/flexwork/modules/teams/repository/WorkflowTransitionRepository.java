package io.flexwork.modules.teams.repository;

import io.flexwork.modules.teams.domain.WorkflowState;
import io.flexwork.modules.teams.domain.WorkflowTransition;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkflowTransitionRepository extends JpaRepository<WorkflowTransition, Long> {
    /**
     * Finds all valid target workflow states for a given workflow and current state.
     *
     * @param workflowId the ID of the workflow
     * @param sourceStateName the name of the current state
     * @return a list of WorkflowState objects representing valid target states
     */
    @Query(
            "SELECT ws FROM WorkflowState ws "
                    + "JOIN WorkflowTransition wt ON ws.stateName = wt.targetState "
                    + "WHERE wt.workflow.id = :workflowId AND wt.sourceState = :sourceStateName")
    List<WorkflowState> findValidTargetStates(
            @Param("workflowId") Long workflowId, @Param("sourceStateName") String sourceStateName);
}
