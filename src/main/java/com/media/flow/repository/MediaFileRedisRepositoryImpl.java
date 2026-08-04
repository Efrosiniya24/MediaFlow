package com.media.flow.repository;

import com.media.flow.constant.RedisKeys;
import com.media.flow.model.MediaFile;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

/**
 * @author yefrosiniya.zinkovskaya
 * @since 02.08.2026
 */
@Repository
@RequiredArgsConstructor
public class MediaFileRedisRepositoryImpl implements MediaFileRedisRepository {
    private static final Duration FAMILY_TTL = Duration.ofMinutes(30);

    private final RedisTemplate<String, MediaFile> redisTemplate;
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public Instant save(final MediaFile mediaFile) {
        final UUID mediaId = mediaFile.getId();
        final UUID originFileId = mediaFile.getOriginFileId();

        saveMediaFileData(mediaId, mediaFile, originFileId);
        return setMediaFileFamilyExpirations(originFileId);
    }

    private void saveMediaFileData(final UUID mediaId, final MediaFile mediaFile, final UUID originFileId) {
        redisTemplate.opsForHash()
            .put(
                RedisKeys.media(originFileId),
                mediaId.toString(),
                mediaFile
            );
    }

    private Instant setMediaFileFamilyExpirations(final UUID originFileId) {
        final Instant expiresAt = Instant.now().plus(FAMILY_TTL);
        stringRedisTemplate.opsForZSet()
            .add(RedisKeys.EXPIRATIONS,
                originFileId.toString(),
                expiresAt.getEpochSecond());
        return expiresAt;
    }
}
