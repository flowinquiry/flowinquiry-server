package io.flexwork.modules.teams.repository;

import io.flexwork.modules.teams.domain.WorkflowTransitionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkflowTransitionHistoryRepository
        extends JpaRepository<WorkflowTransitionHistory, Long> {}
