package io.playqd.mediaserver.api.rest.controller;

import io.playqd.mediaserver.config.upnp.ConditionalOnUpnpEnabled;
import io.playqd.mediaserver.persistence.BrowsableObjectDao;
import io.playqd.mediaserver.service.upnp.service.contentdirectory.UpnpClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping(RestApiResources.BROWSABLE)
@ConditionalOnUpnpEnabled
class UpnpBrowsableObjectController {

  private final BrowsableObjectDao browsableObjectDao;

  UpnpBrowsableObjectController(BrowsableObjectDao browsableObjectDao) {
    this.browsableObjectDao = browsableObjectDao;
  }

  @GetMapping(path = "/image/{objectId}", produces = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE})
  ResponseEntity<byte[]> getBrowsableObjectImage(@PathVariable String objectId) {
    var mayBeAlbumArt = getFromBrowsableObject(objectId);
    return mayBeAlbumArt
        .map(albumArt -> ResponseEntity
            .ok()
            .cacheControl(CacheControl.maxAge(1, TimeUnit.HOURS))
            .body(albumArt))
        .orElseGet(() -> ResponseEntity
            .notFound()
            .build());
  }

  private Optional<byte[]> getFromBrowsableObject(String objectId) {
    return browsableObjectDao.getOneByObjectId(objectId)
        .filter(obj -> UpnpClass.image == obj.upnpClass())
        .map(obj -> {
          try {
            return Files.readAllBytes(obj.location());
          } catch (IOException e) {
            log.error("Failed to read image content.", e);
            return new byte[0];
          }
        })
        .filter(bytes -> bytes.length > 0);
  }

}
