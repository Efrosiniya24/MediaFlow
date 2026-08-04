package com.media.flow.repository;

import com.media.flow.model.MediaFile;

import java.time.Instant;

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
}
