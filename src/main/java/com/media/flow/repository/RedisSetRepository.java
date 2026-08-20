package com.media.flow.repository;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

/**
 * @author yefrosiniya.zinkovskaya
 * @since 11.08.2026
 */
public interface RedisSetRepository {

    Instant setMediaFileFamilyExpirations(UUID originFileId);

    /**
     * Finds files set with expired ids (more than 30 min of set life)
     *
     * @param now time of starting cleanup
     * @return expired origin file ids set
     */
    Set<String> findExpiredOriginFileId(final Instant now);

    void delete(Set<String> expiredIds);
}
