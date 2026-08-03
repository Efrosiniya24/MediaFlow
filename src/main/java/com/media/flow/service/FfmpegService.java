package com.media.flow.service;

import com.media.flow.dto.MediaFileDto;

import java.nio.file.Path;

public interface FfmpegService {
    /**
     * Extracts media metadata using ffprobe
     *
     * @param filePath path to the media file to probe
     * @return a dto with the main media parameters
     */
    MediaFileDto getVideoParams(final Path filePath);
}
