package io.flexwork.modules.teams.repository;

import io.flexwork.modules.teams.domain.WorkflowTransition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkflowTransitionRepository extends JpaRepository<WorkflowTransition, Long> {}
