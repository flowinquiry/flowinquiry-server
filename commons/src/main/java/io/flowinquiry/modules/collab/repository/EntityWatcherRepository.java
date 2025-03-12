package io.flowinquiry.modules.collab.repository;

import io.flowinquiry.modules.collab.domain.EntityType;
import io.flowinquiry.modules.collab.domain.EntityWatcher;
import jakarta.persistence.QueryHint;
import java.util.Collection;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface EntityWatcherRepository extends JpaRepository<EntityWatcher, Long> {

    @EntityGraph(attributePaths = {"watchUser"})
    @QueryHints({
        @QueryHint(name = "org.hibernate.cacheable", value = "true"),
        @QueryHint(name = "org.hibernate.cacheRegion", value = "queryWatchers")
    })
    List<EntityWatcher> findByEntityTypeAndEntityId(EntityType entityType, Long entityId);

    @QueryHints({
        @QueryHint(name = "org.hibernate.cacheable", value = "true"),
        @QueryHint(name = "org.hibernate.cacheRegion", value = "queryWatchers")
    })
    @Query(
            "SELECT ew.watchUser.id FROM EntityWatcher ew WHERE ew.entityType = :entityType AND ew.entityId = :entityId")
    List<Long> findWatcherIdsByEntity(
            @Param("entityType") EntityType entityType, @Param("entityId") Long entityId);

    @QueryHints({
        @QueryHint(name = "org.hibernate.cacheable", value = "true"),
        @QueryHint(name = "org.hibernate.cacheRegion", value = "queryWatchers")
    })
    boolean existsByEntityTypeAndEntityIdAndWatchUserId(
            EntityType entityType, Long entityId, Long watchUserId);

    @QueryHints({
        @QueryHint(name = "org.hibernate.cacheable", value = "true"),
        @QueryHint(name = "org.hibernate.cacheRegion", value = "queryWatchers")
    })
    Page<EntityWatcher> findByWatchUserId(Long userId, Pageable pageable);

    void deleteByEntityTypeAndEntityIdAndWatchUserId(
            EntityType entityType, Long entityId, Long watchUserId);

    @Modifying
    @Transactional
    void deleteByEntityTypeAndEntityIdAndWatchUser_IdIn(
            EntityType entityType, Long entityId, Collection<Long> watchUserIds);
}
