package io.flowinquiry.modules.shared.service.cache;

import java.time.Duration;

/**
 * A service interface for deduplication caching.
 *
 * <p>This cache helps prevent duplicate operations by storing and checking unique keys. It supports
 * time-based expiration for automatic key removal.
 */
public interface DeduplicationCacheService {

    /**
     * Checks if a given key is already stored in the cache.
     *
     * <p>If the key exists, it indicates that the corresponding operation has already been
     * processed and should not be repeated.
     *
     * @param key the unique identifier to check in the cache.
     * @return {@code true} if the key exists, otherwise {@code false}.
     */
    boolean containsKey(String key);

    /**
     * Stores a key in the cache with an expiration time.
     *
     * <p>The key will remain in the cache until the specified expiration duration has elapsed,
     * after which it will be automatically removed.
     *
     * @param key the unique identifier to store in the cache.
     * @param expirationDuration the duration after which the key should expire.
     */
    void put(String key, Duration expirationDuration);
}
