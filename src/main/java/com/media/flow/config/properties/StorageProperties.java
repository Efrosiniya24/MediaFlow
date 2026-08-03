package com.media.flow.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author yefrosiniya.zinkovskaya
 * @since 29.07.2026
 */
@Data
@Configuration
@ConfigurationProperties("storage")
public class StorageProperties {
    private String tempDir;
    private String permanentDir;
}
