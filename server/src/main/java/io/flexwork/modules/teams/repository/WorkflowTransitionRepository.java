package io.flexwork.modules.teams.repository;

import io.flexwork.modules.teams.domain.WorkflowState;
import io.flexwork.modules.teams.domain.WorkflowTransition;
import java.util.List;
import java.util.Optional;
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
     * @param sourceStateId the name of the current state
     * @return a list of WorkflowState objects representing valid target states
     */
    @Query(
            "SELECT ws FROM WorkflowState ws "
                    + "JOIN WorkflowTransition wt ON ws.id = wt.targetState.id "
                    + "WHERE wt.workflow.id = :workflowId AND wt.sourceState.id = :sourceStateId")
    List<WorkflowState> findValidTargetStates(
            @Param("workflowId") Long workflowId, @Param("sourceStateId") Long sourceStateId);

    /**
     * @param workflowId
     * @param sourceStateId
     * @return
     */
    @Query(
            "SELECT wt "
                    + "FROM WorkflowTransition wt "
                    + "WHERE wt.sourceState.id = :sourceStateId "
                    + "AND wt.workflow.id = :workflowId")
    List<WorkflowTransition> findTransitionsBySourceState(
            @Param("workflowId") Long workflowId, @Param("sourceStateId") Long sourceStateId);

    /**
     * @param workflowId
     * @param sourceStateId
     * @param targetStateId
     * @return
     */
    Optional<WorkflowTransition> findByWorkflowIdAndSourceStateIdAndTargetStateId(
            Long workflowId, Long sourceStateId, Long targetStateId);
}
