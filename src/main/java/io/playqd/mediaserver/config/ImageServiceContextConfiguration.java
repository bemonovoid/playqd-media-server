package io.playqd.mediaserver.config;

import io.playqd.mediaserver.config.properties.PlayqdProperties;
import io.playqd.mediaserver.persistence.AudioFileDao;
import io.playqd.mediaserver.service.metadata.ImageService;
import io.playqd.mediaserver.service.metadata.ImageServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ImageServiceContextConfiguration {

  @Bean
  ImageService imageService(PlayqdProperties playqdProperties, AudioFileDao audioFileDao) {
    return new ImageServiceImpl(playqdProperties, audioFileDao);
  }

}
