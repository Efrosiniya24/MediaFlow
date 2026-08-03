package com.media.flow.record;

import java.nio.file.Path;
import java.util.UUID;

/**
 * @author yefrosiniya.zinkovskaya
 * @since 31.07.2026
 */
public record StoredMedia(Path localPath, UUID id, String originalName) {
}
