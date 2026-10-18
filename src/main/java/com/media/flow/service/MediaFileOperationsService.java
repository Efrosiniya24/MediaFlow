package com.media.flow.service;

import com.media.flow.dto.MediaFileDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

/**
 * @author yefrosiniya.zinkovskaya
 * @since 29.07.2026
 */
public interface MediaFileOperationsService {

    /**
     * Uploads a media file, extracts its metadata, persists it to storage
     * and stores its metadata in Redis
     *
     * @param file the file to upload and analyze
     * @return a dto with media metadata, identifiers, and expiration time
     */
    MediaFileDto uploadAndGetMediaFileData(MultipartFile file);

    /**
     * Clean up redis and the storage from expired files
     */
    void cleanUpMediaFiles();

    /**
     * Downloads file from storage
     *
     * @param originalFileId original file id
     * @param id             file id
     * @return file data for downloading
     */
    DownloadResult getDataForDownloading(UUID originalFileId, UUID id);
}
