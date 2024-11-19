package io.flexwork.modules.teams.repository;

import io.flexwork.modules.teams.domain.WorkflowAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkflowActionRepository extends JpaRepository<WorkflowAction, Long> {}
