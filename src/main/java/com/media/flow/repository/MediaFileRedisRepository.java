package com.media.flow.repository;

import com.media.flow.model.MediaFile;

import java.time.LocalDateTime;

/**
 * @author yefrosiniya.zinkovskaya
 * @since 02.08.2026
 */
public interface MediaFileRedisRepository {
    /**
     * Saves media file metadata, adds the file id to its family set,
     * and refreshes the shared family expiration
     *
     * @param mediaFile media file to save
     * @return the new family expiration time
     */
    LocalDateTime save(MediaFile mediaFile);
}
