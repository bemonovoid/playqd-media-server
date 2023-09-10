package io.playqd.mediaserver.persistence.simple;

import io.playqd.mediaserver.commons.JpaDaoTest;
import io.playqd.mediaserver.config.MediaLibraryContextConfiguration;
import io.playqd.mediaserver.config.MediaSourceContextConfiguration;
import io.playqd.mediaserver.persistence.MediaSourceDao;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

@Import({ MediaLibraryContextConfiguration.class, MediaSourceContextConfiguration.class })
class MediaSourceDaoImplTest extends JpaDaoTest {

  @Test
  void hasMediaSourceInitializedFromProperties(@Autowired MediaSourceDao mediaSourceDao) {
    var mediaSources = mediaSourceDao.getAll();
    Assertions.assertFalse(mediaSources.isEmpty());
    var mediaSource = mediaSources.get(0);
    Assertions.assertNotNull(mediaSourceDao.get(mediaSource.id()));
  }

}