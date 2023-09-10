package io.playqd.mediaserver.config;

import io.playqd.mediaserver.config.lifecycle.ApplicationRunnerOrder;
import io.playqd.mediaserver.config.lifecycle.MediaSourceInitializer;
import io.playqd.mediaserver.config.properties.PlayqdProperties;
import io.playqd.mediaserver.persistence.AudioFileDao;
import io.playqd.mediaserver.persistence.MediaSourceDao;
import io.playqd.mediaserver.persistence.simple.MediaSourceDaoImpl;
import io.playqd.mediaserver.service.mediasource.MediaSourceFileSystemWatcherService;
import io.playqd.mediaserver.service.mediasource.MediaSourceScanner;
import io.playqd.mediaserver.service.mediasource.MediaSourceScannerImpl;
import io.playqd.mediaserver.service.mediasource.MediaSourceService;
import io.playqd.mediaserver.service.mediasource.MediaSourceServiceImpl;
import io.playqd.mediaserver.service.mediasource.MediaSourceWatcherService;
import io.playqd.mediaserver.service.metadata.FileAttributesReader;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

@Configuration
public class MediaSourceContextConfiguration {

  @Bean
  MediaSourceDao mediaSourceDao() {
    return new MediaSourceDaoImpl();
  }

  @Bean
  MediaSourceService mediaSourceService(MediaSourceDao mediaSourceDao) {
    return new MediaSourceServiceImpl(mediaSourceDao);
  }

  @Bean
  MediaSourceScanner mediaSourceScanner(AudioFileDao audioFileDao,
                                        MediaSourceDao mediaSourceDao,
                                        FileAttributesReader fileMetadataReader) {
    return new MediaSourceScannerImpl(audioFileDao, mediaSourceDao, fileMetadataReader);
  }

  @Bean
  MediaSourceWatcherService mediaSourceWatcherService(ApplicationEventPublisher eventPublisher) {
    return new MediaSourceFileSystemWatcherService(eventPublisher);
  }

  @Bean
  @Order(ApplicationRunnerOrder.MEDIA_SOURCE_INITIALIZER)
  ApplicationRunner mediaSourceInitializer(PlayqdProperties playqdProperties,
                                           MediaSourceService mediaSourceService,
                                           MediaSourceScanner mediaSourceScanner,
                                           MediaSourceWatcherService mediaSourceWatcherService) {
    return new MediaSourceInitializer(
        playqdProperties, mediaSourceService, mediaSourceScanner, mediaSourceWatcherService);
  }

}
