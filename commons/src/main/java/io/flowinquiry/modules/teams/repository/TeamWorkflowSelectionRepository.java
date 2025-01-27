package io.flowinquiry.modules.teams.repository;

import io.flowinquiry.modules.teams.domain.TeamWorkflowSelection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface TeamWorkflowSelectionRepository
        extends JpaRepository<TeamWorkflowSelection, Long> {

    @Modifying
    @Transactional
    @Query(
            value = "DELETE FROM fw_team_workflow_selection WHERE workflow_id = :workflowId",
            nativeQuery = true)
    void deleteByWorkflowId(@Param("workflowId") Long workflowId);

    boolean existsByTeamIdAndWorkflowId(Long teamId, Long workflowId);

    @Transactional
    void deleteByTeamIdAndWorkflowId(Long teamId, Long workflowId);
}
