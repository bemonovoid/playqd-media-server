package io.playqd.mediaserver.persistence.jpa.dao;

import io.playqd.mediaserver.commons.JpaDaoTest;
import io.playqd.mediaserver.config.ApplicationConfiguration;
import io.playqd.mediaserver.config.upnp.UpnpDaoContextConfiguration;
import io.playqd.mediaserver.persistence.BrowsableObjectDao;
import io.playqd.mediaserver.service.upnp.service.contentdirectory.UpnpClass;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Import({ UpnpDaoContextConfiguration.class})
class JpaBrowsableObjectDaoTest extends JpaDaoTest {

  @Test
  void saveRootBrowsableObject(@Autowired BrowsableObjectDao browsableObjectDao) {
    var browsableObject = browsableObjectDao.save(browsableObjectSetter -> {
      browsableObjectSetter.setDcTitle("title");
      browsableObjectSetter.setUpnpClass(UpnpClass.storageFolder);
      browsableObjectSetter.setLocation("/test");
    });
    assertNotNull(browsableObject);
    assertNull(browsableObject.parentId());
    assertEquals(browsableObject.childCount().get(), 0);
    assertEquals(browsableObject.childContainerCount(), 0);
  }

  @Test
  void saveWithoutLocationFails(@Autowired BrowsableObjectDao browsableObjectDao) {
    var executable = (Executable) () -> browsableObjectDao.save(browsableObjectSetter -> {
      browsableObjectSetter.setDcTitle("title");
      browsableObjectSetter.setUpnpClass(UpnpClass.storageFolder);
    });
    assertThrows(IllegalArgumentException.class, executable);
  }
}