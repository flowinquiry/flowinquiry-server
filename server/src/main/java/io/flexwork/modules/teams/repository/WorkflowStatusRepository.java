package io.flexwork.modules.teams.repository;

import io.flexwork.modules.teams.domain.WorkflowStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkflowStatusRepository extends JpaRepository<WorkflowStatus, Long> {}
