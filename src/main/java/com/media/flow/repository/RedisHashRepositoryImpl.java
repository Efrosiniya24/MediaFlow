package com.media.flow.repository;

import com.media.flow.constant.RedisKeys;
import com.media.flow.model.MediaFile;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * @author yefrosiniya.zinkovskaya
 * @since 11.08.2026
 */
@Repository
@RequiredArgsConstructor
public class RedisHashRepositoryImpl implements RedisHashRepository {

    private final RedisTemplate<String, MediaFile> redisTemplate;

    @Override
    public void saveMediaFileData(final UUID mediaId, final MediaFile mediaFile, final UUID originFileId) {
        redisTemplate.opsForHash()
            .put(
                RedisKeys.media(originFileId),
                mediaId.toString(),
                mediaFile
            );
    }
}
