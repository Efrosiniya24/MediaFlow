package com.media.flow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author yefrosiniya.zinkovskaya
 * @since 26.07.2026
 */
@SpringBootApplication
@EnableScheduling
public class MediaFlowApplication {

    public static void main(String[] args) {
        SpringApplication.run(MediaFlowApplication.class, args);
    }

}
