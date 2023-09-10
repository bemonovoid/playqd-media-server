package io.playqd.mediaserver.config;

import io.playqd.mediaserver.config.lifecycle.ApplicationRunnerOrder;
import io.playqd.mediaserver.config.lifecycle.PlaylistsDirectoryInitializer;
import io.playqd.mediaserver.config.properties.PlayqdProperties;
import io.playqd.mediaserver.persistence.AudioFileDao;
import io.playqd.mediaserver.service.playlist.PlaylistService;
import io.playqd.mediaserver.service.playlist.PlaylistServiceImpl;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

@Configuration
public class PlaylistContextConfiguration {

  @Bean
  @Order(ApplicationRunnerOrder.PLAYLISTS_DIRECTORY_INITIALIZER)
  ApplicationRunner playlistsDirectoryInitializer(PlayqdProperties playqdProperties) {
    return new PlaylistsDirectoryInitializer(playqdProperties);
  }

  @Bean
  PlaylistService playlistService(PlayqdProperties playqdProperties, AudioFileDao audioFileDao) {
    return new PlaylistServiceImpl(playqdProperties, audioFileDao);
  }

}
