package io.flexwork.modules.collab.repository;

import io.flexwork.modules.collab.domain.TeamRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamRoleRepository extends JpaRepository<TeamRole, String> {}
