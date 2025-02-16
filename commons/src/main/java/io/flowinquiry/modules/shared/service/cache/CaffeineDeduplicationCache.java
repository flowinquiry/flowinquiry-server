package io.flowinquiry.modules.shared.service.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.flowinquiry.modules.shared.domain.DeduplicationCacheEntry;
import io.flowinquiry.modules.shared.reppository.DeduplicationCacheRepository;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;
import org.springframework.stereotype.Service;

@Service
public class CaffeineDeduplicationCache implements DeduplicationCacheService {

    private final DeduplicationCacheRepository deduplicationCacheRepository;

    private final Cache<String, Instant> cache;

    public CaffeineDeduplicationCache(DeduplicationCacheRepository deduplicationCacheRepository) {
        this.deduplicationCacheRepository = deduplicationCacheRepository;
        cache =
                Caffeine.newBuilder()
                        .maximumSize(500) // Auto-evicts if more than 500
                        .expireAfterWrite(24, TimeUnit.HOURS) // Expire after 24 hours
                        .removalListener(
                                (key, value, cause) -> {
                                    // ✅ If entry is evicted, store in the database
                                    if (key != null && value != null) {
                                        String evictedKey = (String) key;
                                        Instant expiredTime = (Instant) value;
                                        deduplicationCacheRepository.save(
                                                new DeduplicationCacheEntry(
                                                        evictedKey, expiredTime));
                                    }
                                })
                        .build();
    }

    @Override
    public boolean containsKey(String key) {
        if (cache.getIfPresent(key) != null) {
            return true;
        }

        // ✅ If Not in Cache, Check Database
        boolean exists = deduplicationCacheRepository.existsByKey(key);
        if (exists) {
            cache.put(key, Instant.now().plus(24, ChronoUnit.HOURS)); // Restore to memory
        }
        return exists;
    }

    @Override
    public void put(String key, Duration expirationDuration) {
        Instant expiredTime = Instant.now().plus(expirationDuration);

        // ✅ Store in Caffeine
        cache.put(key, expiredTime);

        // ✅ If Cache is Full, Persist to Database
        if (cache.estimatedSize() > 400) {
            deduplicationCacheRepository.save(new DeduplicationCacheEntry(key, expiredTime));
        }
    }
}
