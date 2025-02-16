package io.flowinquiry.modules.shared.reppository;

import io.flowinquiry.modules.shared.domain.DeduplicationCacheEntry;
import java.time.Instant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface DeduplicationCacheRepository
        extends JpaRepository<DeduplicationCacheEntry, String> {
    boolean existsByKey(String key);

    @Modifying
    @Transactional
    @Query("DELETE FROM DeduplicationCacheEntry e WHERE e.expiredTime < :now")
    int deleteExpiredEntries(@Param("now") Instant now);
}
