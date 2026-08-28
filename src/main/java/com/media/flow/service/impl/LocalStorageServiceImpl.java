package com.media.flow.service.impl;

import com.media.flow.config.properties.StorageProperties;
import com.media.flow.exception.StorageException;
import com.media.flow.record.StoredMedia;
import com.media.flow.service.LocalStorageService;
import com.media.flow.utils.DateUtils;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * @author yefrosiniya.zinkovskaya
 * @since 29.07.2026
 */
@ConditionalOnProperty(name = "storage.type", havingValue = "local")
@Slf4j
@Service
@RequiredArgsConstructor
public class LocalStorageServiceImpl implements LocalStorageService {
    private final StorageProperties storageProperties;

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(Paths.get(storageProperties.getTempDir()));
            Files.createDirectories(Paths.get(storageProperties.getPermanentDir()));
        } catch (final IOException ex) {
            log.error("Failed to initialize storage directories", ex);
            throw new StorageException("Failed to initialize storage directories");
        }
    }

    @Override
    public StoredMedia saveTemporaryFile(final MultipartFile file) {
        return saveToDirectory(file, storageProperties.getTempDir());
    }

    @Override
    public String savePersistFile(final StoredMedia temporary, final UUID originFileId) {
        return moveToPermanentDirectory(temporary.localPath(), originFileId).toString();
    }

    @Override
    public void deleteFile(final StoredMedia storedMedia) {
        if (Objects.isNull(storedMedia)) {
            return;
        }
        try {
            Files.deleteIfExists(storedMedia.localPath());
        } catch (final IOException ex) {
            log.error("Failed to delete file", ex);
            throw new StorageException("Failed to delete file");
        }
    }

    @Override
    public void deleteDirectoryById(final String originFileId) {
        final Path path = findDirectoryPath(originFileId);
        if (Objects.isNull(path)) {
            return;
        }
        deleteDirectoryAndFiles(path);
    }

    @Override
    public void deleteDirectoryByDate(final String date) {
        final Path path = getPath(date);
        if (!Files.isDirectory(path)) {
            return;
        }
        deleteDirectoryAndFiles(path);
    }

    @Override
    public void cleanUpOrphanedStorageDirectories() {
        final LocalDate yesterday = LocalDate.now().minusDays(2);
        final Path permanentDirectory = Path.of(storageProperties.getPermanentDir());

        try (final Stream<Path> dirs = Files.list(permanentDirectory)) {
            dirs.filter(Files::isDirectory)
                .filter(dir -> {
                    final LocalDate directoryDate = LocalDate.parse(dir.getFileName().toString());
                    return directoryDate.isBefore(yesterday);
                })
                .map(dir -> dir.getFileName().toString())
                .forEach(this::deleteDirectoryByDate);
        } catch (final IOException ex) {
            log.error("Failed to clean up orphaned storage directories", ex);
            throw new StorageException("Failed to clean up orphaned storage directories");
        }
    }

    private void deleteDirectoryAndFiles(final Path path) {
        try (final Stream<Path> walk = Files.walk(path)) {
            walk.sorted(Comparator.reverseOrder())
                .forEach(this::deletePath);
        } catch (final IOException ex) {
            log.error("Failed to delete directory {}", path, ex);
            throw new StorageException("Failed to delete directory");
        }
    }

    private Path findDirectoryPath(final String originFileId) {
        final Path todayDirectoryPath = getPath(originFileId, DateUtils.getCurrentDate());
        if (Files.isDirectory(todayDirectoryPath)) {
            return todayDirectoryPath;
        }

        final Path yesterdayDirectoryPath = getPath(originFileId, DateUtils.getYesterdayDate());
        if (Files.isDirectory(yesterdayDirectoryPath)) {
            return yesterdayDirectoryPath;
        }
        return null;
    }

    private Path getPath(final String originFileId, final String date) {
        return Paths.get(
            storageProperties.getPermanentDir(),
            date,
            originFileId
        );
    }

    private Path getPath(final String date) {
        return Paths.get(
            storageProperties.getPermanentDir(),
            date
        );
    }

    private void deletePath(final Path path) {
        try {
            Files.deleteIfExists(path);
        } catch (final IOException ex) {
            throw new StorageException("Failed to delete path: " + path);
        }
    }

    private StoredMedia saveToDirectory(final MultipartFile file, final String directory) {
        final UUID id = UUID.randomUUID();
        final String originalName = sanitizeFileName(file.getOriginalFilename());
        final Path filePath = Paths
            .get(directory)
            .resolve(id + "_" + originalName);
        try {
            file.transferTo(filePath);
            return new StoredMedia(filePath, id, originalName);
        } catch (final IOException ex) {
            log.error("Failed to save uploaded file", ex);
            throw new StorageException("Failed to save uploaded file");
        }
    }

    private String sanitizeFileName(final String originalFilename) {
        if (Objects.isNull(originalFilename) || originalFilename.isBlank()) {
            return "upload";
        }
        return Paths.get(originalFilename).getFileName().toString();
    }

    private Path moveToPermanentDirectory(final Path tempPath, final UUID originalFileId) {
        final Path directory = createDirectory(originalFileId);

        final Path permanentPath = directory.resolve(tempPath.getFileName());
        try {
            return Files.move(tempPath, permanentPath);
        } catch (final IOException ex) {
            log.error("Failed to move file to permanent storage", ex);
            throw new StorageException("Failed to move file to permanent storage");
        }
    }

    private Path createDirectory(final UUID originalFileId) {
        final Path directory = Paths.get(
            storageProperties.getPermanentDir(),
            DateUtils.getCurrentDate(),
            originalFileId.toString()
        );
        try {
            return Files.createDirectories(directory);
        } catch (final IOException ex) {
            log.error("Failed to create directory", ex);
            throw new StorageException("Failed to create directory");
        }
    }
}
