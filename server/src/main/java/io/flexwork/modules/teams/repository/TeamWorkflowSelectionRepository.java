package io.flexwork.modules.teams.repository;

import io.flexwork.modules.teams.domain.TeamWorkflowSelection;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamWorkflowSelectionRepository
        extends JpaRepository<TeamWorkflowSelection, Long> {}
