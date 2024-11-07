package io.flexwork.modules.teams.repository;

import io.flexwork.modules.teams.domain.WorkflowTransition;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkflowTransitionRepository extends JpaRepository<WorkflowTransition, Long> {}
