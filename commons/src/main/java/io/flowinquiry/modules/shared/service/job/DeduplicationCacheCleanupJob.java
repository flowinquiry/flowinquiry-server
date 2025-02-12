package io.flowinquiry.modules.shared.service.job;

import io.flowinquiry.modules.shared.reppository.DeduplicationCacheRepository;
import java.time.ZonedDateTime;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DeduplicationCacheCleanupJob {
    private final DeduplicationCacheRepository deduplicationCacheRepository;

    public DeduplicationCacheCleanupJob(DeduplicationCacheRepository deduplicationCacheRepository) {
        this.deduplicationCacheRepository = deduplicationCacheRepository;
    }

    /**
     * Scheduled job to remove expired cache entries from the database.
     *
     * <p>Runs daily at midnight to ensure old cache keys are removed.
     */
    @Scheduled(cron = "0 0 0 * * ?") // Runs daily at midnight
    @SchedulerLock(name = "DeduplicationCacheCleanupJob")
    public void cleanupExpiredCacheEntries() {
        int deletedRows = deduplicationCacheRepository.deleteExpiredEntries(ZonedDateTime.now());
        log.info("Deleted {} expired cache entries", deletedRows);
    }
}
