package io.playqd.mediaserver.persistence.jpa.dao;

import io.playqd.mediaserver.persistence.BrowsableObjectDao;
import io.playqd.mediaserver.persistence.jpa.JpaDaoTest;
import io.playqd.mediaserver.persistence.jpa.repository.BrowsableObjectRepository;
import io.playqd.mediaserver.service.upnp.server.service.contentdirectory.UpnpClass;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JpaBrowsableObjectDaoTest extends JpaDaoTest {

  private final BrowsableObjectDao browsableObjectDao;

  JpaBrowsableObjectDaoTest(@Autowired BrowsableObjectRepository browsableObjectRepository) {
      this.browsableObjectDao = new JpaBrowsableObjectDao(browsableObjectRepository);
  }

  @Test
  void saveRootBrowsableObject() {
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
  void saveWithoutLocationFails() {
    var executable = (Executable) () -> browsableObjectDao.save(browsableObjectSetter -> {
      browsableObjectSetter.setDcTitle("title");
      browsableObjectSetter.setUpnpClass(UpnpClass.storageFolder);
    });
    assertThrows(IllegalArgumentException.class, executable);
  }
}