package io.flexwork.modules.teams.repository;

import io.flexwork.modules.teams.domain.TeamRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamRequestStatusRepository extends JpaRepository<TeamRequestStatus, Long> {}
