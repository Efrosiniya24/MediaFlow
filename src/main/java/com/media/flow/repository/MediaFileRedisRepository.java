package com.media.flow.repository;

import com.media.flow.model.MediaFile;

import java.time.Instant;
import java.util.Set;

/**
 * @author yefrosiniya.zinkovskaya
 * @since 02.08.2026
 */
public interface MediaFileRedisRepository {
    /**
     * Saves media file metadata into the family hash
     * and refreshes the shared family expiration
     *
     * @param mediaFile media file to save
     * @return the new family expiration time
     */
    Instant save(MediaFile mediaFile);

    /**
     * Finds files set with expired ids (more than 30 min of set life)
     *
     * @param now time of starting cleanup
     * @return expired origin file ids set
     */
    Set<String> findExpiredOriginFileId(final Instant now);

    /**
     * Deletes expired file id from redis hash
     *
     * @param expiredIds deleted expired file id
     */
    void deleteFromHash(final Set<String> expiredIds);

    void deleteFromSet(Set<String> expiredIds);
}
