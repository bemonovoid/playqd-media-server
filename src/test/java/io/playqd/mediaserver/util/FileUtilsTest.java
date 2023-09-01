package io.playqd.mediaserver.util;

import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class FileUtilsTest {

  @Test
  void getFileExtension() throws Exception {
    var url = FileUtilsTest.class.getResource("/files/testFileExtension.txt");
    assertNotNull(url);
    var file = new File(url.toURI());
    assertEquals("txt", FileUtils.getFileExtension(file));
  }

}