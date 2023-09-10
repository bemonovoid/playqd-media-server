package io.playqd.mediaserver.config;

import io.playqd.mediaserver.config.properties.PlayqdProperties;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@EnableAsync
@EnableAutoConfiguration
@EnableConfigurationProperties
public class ApplicationConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "playqd")
    PlayqdProperties playqdProperties() {
        return new PlayqdProperties();
    }

}
