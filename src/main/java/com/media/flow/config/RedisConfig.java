package com.media.flow.config;

import com.media.flow.model.MediaFile;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @author yefrosiniya.zinkovskaya
 * @since 02.08.2026
 */
@Configuration
public class RedisConfig {
    @Bean
    public RedisTemplate<String, MediaFile> mediaFileRedisTemplate(final RedisConnectionFactory redisConnectionFactory) {
        final RedisSerializer<Object> serializer = GenericJacksonJsonRedisSerializer.builder().build();
        final RedisTemplate<String, MediaFile> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(serializer);
        redisTemplate.setHashValueSerializer(serializer);
        return redisTemplate;
    }
}
