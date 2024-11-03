package io.flexwork.modules.usermanagement.repository;

import io.flexwork.modules.usermanagement.domain.Team;
import io.flexwork.modules.usermanagement.service.dto.TeamDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long>, JpaSpecificationExecutor<Team> {
    @Query(
            "SELECT new io.flexwork.modules.usermanagement.service.dto.TeamDTO(t.id, t.name, t.logoUrl, t.slogan, t.description, t.organization.id, COUNT(m.id)) "
                    + "FROM Team t LEFT JOIN t.members m "
                    + "GROUP BY t.id")
    Page<TeamDTO> findAllDTOs(Specification<Team> spec, Pageable pageable);
}
