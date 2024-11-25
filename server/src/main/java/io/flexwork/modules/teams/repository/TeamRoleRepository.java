package io.flexwork.modules.teams.repository;

import io.flexwork.modules.teams.domain.TeamRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamRoleRepository extends JpaRepository<TeamRole, String> {}
