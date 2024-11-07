package io.flexwork.modules.teams.repository;

import io.flexwork.modules.teams.domain.WorkflowTransitionHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkflowTransitionHistoryRepository
        extends JpaRepository<WorkflowTransitionHistory, Long> {}
