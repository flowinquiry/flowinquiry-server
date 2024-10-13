package io.flexwork.modules.usermanagement.repository;

import io.flexwork.modules.usermanagement.domain.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {}
