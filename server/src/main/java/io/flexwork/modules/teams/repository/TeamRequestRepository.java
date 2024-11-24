package io.flexwork.modules.teams.repository;

import io.flexwork.modules.teams.domain.TeamRequest;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamRequestRepository
        extends JpaRepository<TeamRequest, Long>, JpaSpecificationExecutor<TeamRequest> {

    @EntityGraph(attributePaths = {"team", "requestUser", "assignUser", "workflow"})
    Page<TeamRequest> findAll(Specification<TeamRequest> spec, Pageable pageable);

    @Query(
            value =
                    """
        SELECT tr
        FROM TeamRequest tr
        WHERE tr.team.id = (
            SELECT r.team.id
            FROM TeamRequest r
            WHERE r.id = :requestId
        )
        AND tr.id < :requestId
        ORDER BY tr.id DESC
            LIMIT 1
    """)
    Optional<TeamRequest> findPreviousEntity(@Param("requestId") Long requestId);

    @Query(
            value =
                    """
        SELECT tr
        FROM TeamRequest tr
        WHERE tr.team.id = (
            SELECT r.team.id
            FROM TeamRequest r
            WHERE r.id = :requestId
        )
        AND tr.id > :requestId
        ORDER BY tr.id ASC
            LIMIT 1
    """)
    Optional<TeamRequest> findNextEntity(@Param("requestId") Long requestId);
}
