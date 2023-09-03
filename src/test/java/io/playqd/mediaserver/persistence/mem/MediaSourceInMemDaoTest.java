package io.playqd.mediaserver.persistence.mem;

import io.playqd.mediaserver.config.PlayqdMediaServerConfig;
import io.playqd.mediaserver.config.properties.PlayqdProperties;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = PlayqdMediaServerConfig.class)
@TestPropertySource("classpath:config/application-test.yaml")
class MediaSourceInMemDaoTest {

  @Test
  void hasMediaSourceInitializedFromProperties(@Autowired PlayqdProperties playqdProperties) {
    var mediaSourceDao = new MediaSourceInMemDao(playqdProperties);
    var mediaSources = mediaSourceDao.getAll();
    Assertions.assertFalse(mediaSources.isEmpty());
    var mediaSource = mediaSources.get(0);
    Assertions.assertNotNull(mediaSourceDao.get(mediaSource.id()));
  }

}