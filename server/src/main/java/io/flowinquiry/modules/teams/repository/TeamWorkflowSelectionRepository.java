package io.flowinquiry.modules.teams.repository;

import io.flowinquiry.modules.teams.domain.TeamWorkflowSelection;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamWorkflowSelectionRepository
        extends JpaRepository<TeamWorkflowSelection, Long> {}
