package io.flexwork.modules.teams.repository;

import io.flexwork.modules.teams.domain.WorkflowAction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkflowActionRepository extends JpaRepository<WorkflowAction, Long> {}
