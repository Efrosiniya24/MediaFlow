package com.media.flow.constant;

import java.util.UUID;

/**
 * @author yefrosiniya.zinkovskaya
 * @since 02.08.2026
 */
public final class RedisKeys {
    public static final String EXPIRATIONS = "media:expirations";

    public static String media(final UUID originFileId) {
        return "media:" + originFileId;
    }
}
