package io.flexwork.modules.usermanagement.repository;

import io.flexwork.modules.usermanagement.domain.TeamRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamRoleRepository extends JpaRepository<TeamRole, String> {}
