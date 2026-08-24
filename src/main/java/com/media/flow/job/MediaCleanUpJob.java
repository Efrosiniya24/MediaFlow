package com.media.flow.job;

import com.media.flow.service.MediaFileOperationsService;
import com.media.flow.service.StorageService;
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
    final private StorageService storageService;

    /**
     * Clean up redis and the storage from expired files
     */
    @Scheduled(fixedDelayString = "PT5M")
    public void cleanUpMediaFileFamilies() {
        mediaFileOperationsService.cleanUpMediaFiles();
    }

    /**
     * Checks storage for directories that failed to be deleted on time.
     * Cleans up those directories along with their files, if they exist
     */
    @Scheduled(cron = "0 0 3 * * *")
    public void cleanUpOrphanedStorageDirectories() {
        storageService.cleanUpOrphanedStorageDirectories();
    }
}
