package io.flexwork.modules.teams.repository;

import io.flexwork.modules.teams.domain.TeamRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamRequestRepository extends JpaRepository<TeamRequest, Long> {}
