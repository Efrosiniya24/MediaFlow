package com.media.flow.repository;

import com.media.flow.model.MediaFile;

import java.util.UUID;

/**
 * @author yefrosiniya.zinkovskaya
 * @since 11.08.2026
 */
public interface RedisHashRepository {
    void saveMediaFileData(UUID mediaId, MediaFile mediaFile, UUID originFileId);
}
