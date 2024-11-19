package io.flexwork.modules.teams.repository;

import io.flexwork.modules.teams.domain.WorkflowState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkflowStateRepository extends JpaRepository<WorkflowState, Long> {}
