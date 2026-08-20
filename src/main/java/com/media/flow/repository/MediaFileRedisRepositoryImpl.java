package com.media.flow.repository;

import com.media.flow.constant.RedisKeys;
import com.media.flow.model.MediaFile;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author yefrosiniya.zinkovskaya
 * @since 02.08.2026
 */
@Repository
@RequiredArgsConstructor
public class MediaFileRedisRepositoryImpl implements MediaFileRedisRepository {
    private final RedisTemplate<String, MediaFile> redisTemplate;
    private final RedisHashRepository redisHashRepository;
    private final RedisSetRepository redisSetRepository;

    @Override
    public Instant save(final MediaFile mediaFile) {
        final UUID mediaId = mediaFile.getId();
        final UUID originFileId = mediaFile.getOriginFileId();

        redisHashRepository.saveMediaFileData(mediaId, mediaFile, originFileId);
        return redisSetRepository.setMediaFileFamilyExpirations(originFileId);
    }

    @Override
    public Set<String> findExpiredOriginFileId(final Instant now) {
        return redisSetRepository.findExpiredOriginFileId(now);
    }


    @Override
    public void deleteFromHash(final Set<String> expiredIds) {
        final Set<String> updatedKeys = updateKeys(expiredIds);
        redisTemplate.delete(updatedKeys);
    }

    @Override
    public void deleteFromSet(final Set<String> expiredIds) {
        redisSetRepository.delete(expiredIds);
    }

    private Set<String> updateKeys(final Set<String> expiredIds) {
        return expiredIds.stream()
            .map(UUID::fromString)
            .map(RedisKeys::media)
            .collect(Collectors.toSet());
    }
}
