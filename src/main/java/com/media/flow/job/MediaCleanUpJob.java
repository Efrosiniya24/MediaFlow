package com.media.flow.job;

import com.media.flow.service.MediaFileOperationsService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author yefrosiniya.zinkovskaya
 * @since 10.08.2026
 */
@Component
@RequiredArgsConstructor
public class MediaCleanUpJob {
    final private MediaFileOperationsService mediaFileOperationsService;

    /**
     * Clean up redis and the storage from expired files
     */
    @Scheduled(fixedDelayString = "PT5M")
    public void cleanUpMediaFileFamilies() {
        mediaFileOperationsService.cleanUpMediaFiles();
    }
}
