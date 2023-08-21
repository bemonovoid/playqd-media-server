package io.playqd.mediaserver.config;

import io.playqd.mediaserver.config.properties.PlayqdProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class PlayqdMediaServerConfig {

    @Bean
    @ConfigurationProperties(prefix = "playqd")
    PlayqdProperties playqdProperties() {
        return new PlayqdProperties();
    }

}
