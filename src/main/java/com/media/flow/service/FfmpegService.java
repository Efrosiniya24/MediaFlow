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
    MediaFileDto getVideoParams(Path filePath);

    /**
     * Extracts the first audio stream from a video into the target file.
     *
     * @param sourcePath path to the source video
     * @param targetPath path where the audio file is written
     */
    void extractAudio(Path sourcePath, Path targetPath);
}
