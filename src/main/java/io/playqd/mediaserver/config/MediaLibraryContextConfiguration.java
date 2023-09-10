package io.playqd.mediaserver.config;

import io.playqd.mediaserver.persistence.AudioFileDao;
import io.playqd.mediaserver.persistence.jpa.dao.JpaAudioFileDao;
import io.playqd.mediaserver.persistence.jpa.repository.AudioFileAuditLogRepository;
import io.playqd.mediaserver.persistence.jpa.repository.AudioFileRepository;
import io.playqd.mediaserver.service.jtagger.JTaggerAudioFileAttributesReader;
import io.playqd.mediaserver.service.metadata.FileAttributesReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class MediaLibraryContextConfiguration {

  @Bean
  AudioFileDao audioFileDao(JdbcTemplate jdbcTemplate,
                            AudioFileRepository audioFileRepository,
                            AudioFileAuditLogRepository audioFileAuditLogRepository) {
    return new JpaAudioFileDao(jdbcTemplate, audioFileRepository, audioFileAuditLogRepository);
  }

  @Bean
  FileAttributesReader jTaggerAudioFileAttributesReader() {
    return new JTaggerAudioFileAttributesReader();
  }

}
