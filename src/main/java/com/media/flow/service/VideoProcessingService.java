package com.media.flow.service;

import com.media.flow.dto.MediaFileDto;

import java.util.UUID;

/**
 * @author yefrosiniya.zinkovskaya
 * @since 17.10.2026
 */
public interface VideoProcessingService {
    /**
     * Extracts audio from video.
     * Saves new audio file and updates expiration time in Redis
     *
     * @param originalFileId original file id
     * @param id             file id from which audio is extracted
     * @return extracted audio data
     */
    MediaFileDto extractAudio(UUID originalFileId, UUID id);
}
