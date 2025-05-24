package io.flowinquiry.modules.teams.repository;

import io.flowinquiry.modules.teams.domain.ProjectTicketSequence;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectTicketSequenceRepository
        extends JpaRepository<ProjectTicketSequence, Long> {}
