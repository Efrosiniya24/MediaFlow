package com.media.flow.service;

import com.media.flow.record.StoredMedia;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

/**
 * @author yefrosiniya.zinkovskaya
 * @since 29.07.2026
 */
public interface StorageService {
    /**
     * Saves the uploaded file to a temporary location
     *
     * @param file the file to save temporarily
     * @return the temporary stored media reference
     */
    StoredMedia saveTemporaryFile(MultipartFile file);

    /**
     * Persists a temporary file to durable storage
     *
     * @param temporary    the temporary media to persist
     * @param originFileId originFile id
     * @return the permanent storage location
     */
    String savePersistFile(StoredMedia temporary, UUID originFileId);

    /**
     * Deletes the stored media file
     *
     * @param storedMedia the media to delete
     */
    void deleteFile(StoredMedia storedMedia);

    /**
     * Deletes directory with expired files (files which life more than 30 min)
     *
     * @param originFileId original file id which is also a name of directory
     */
    void deleteDirectoryById(final String originFileId);

    /**
     * Deletes date directory older than yesterday together with their files
     *
     * @param date directory date in yyyy-MM-dd format
     */
    void deleteDirectoryByDate(final String date);

    /**
     * Checks storage for directories that failed to be deleted on time.
     * Cleans up those directories along with their files, if they exist
     */
    void cleanUpOrphanedStorageDirectories();
}
