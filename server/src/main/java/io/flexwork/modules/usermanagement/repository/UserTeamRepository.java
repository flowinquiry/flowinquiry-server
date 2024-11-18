package io.flexwork.modules.usermanagement.repository;

import io.flexwork.modules.usermanagement.domain.UserTeam;
import io.flexwork.modules.usermanagement.domain.UserTeamId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserTeamRepository extends JpaRepository<UserTeam, UserTeamId> {}
