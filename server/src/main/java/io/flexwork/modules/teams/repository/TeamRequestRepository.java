package io.flexwork.modules.teams.repository;

import io.flexwork.modules.teams.domain.TeamRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamRequestRepository
        extends JpaRepository<TeamRequest, Long>, JpaSpecificationExecutor<TeamRequest> {

    @EntityGraph(attributePaths = {"team", "requestUser", "assignUser", "workflow"})
    @Query("SELECT tr FROM TeamRequest tr")
    Page<TeamRequest> findAllWithEagerRelationships(
            Specification<TeamRequest> spec, Pageable pageable);
}
