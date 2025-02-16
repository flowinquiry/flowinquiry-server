package io.flowinquiry.modules.shared.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "fw_deduplication_cache")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeduplicationCacheEntry {
    @Id
    @Column(name = "cache_key", length = 255)
    private String key;

    @Column(name = "expired_time", nullable = false)
    private Instant expiredTime;
}
