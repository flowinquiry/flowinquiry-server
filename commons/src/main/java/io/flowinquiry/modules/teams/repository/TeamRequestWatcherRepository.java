package io.flowinquiry.modules.teams.repository;

import io.flowinquiry.modules.teams.domain.TeamRequestWatcher;
import io.flowinquiry.modules.teams.service.dto.WatcherDTO;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamRequestWatcherRepository extends JpaRepository<TeamRequestWatcher, Long> {

    @Query(
            "SELECT new io.flowinquiry.modules.teams.service.dto.WatcherDTO(u.id, u.firstName, u.lastName, u.imageUrl, u.email) "
                    + "FROM TeamRequestWatcher trw "
                    + "JOIN User u ON trw.userId = u.id "
                    + "WHERE trw.teamRequestId = :teamRequestId")
    List<WatcherDTO> findWatchersByRequestId(@Param("teamRequestId") Long teamRequestId);
}
