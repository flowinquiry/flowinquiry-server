package io.flexwork.modules.teams.repository;

import io.flexwork.modules.teams.domain.Workflow;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkflowRepository extends JpaRepository<Workflow, Long> {}
