package io.playqd.mediaserver.api.rest.controller;

import io.playqd.mediaserver.service.metadata.AlbumArtService;
import io.playqd.mediaserver.service.metadata.ImageSizeRequestParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping(RestControllerApiBasePath.ALBUM_ART)
class AlbumArtController {

    private final AlbumArtService albumArtService;

    AlbumArtController(AlbumArtService albumArtService) {
        this.albumArtService = albumArtService;
    }

    @GetMapping(path = "/{albumId}", produces = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE})
    ResponseEntity<byte[]> get (@PathVariable String albumId,
                                @RequestParam(required = false, name = "size") ImageSizeRequestParam size) {
        var mayBeAlbumArt = albumArtService.get(albumId);
        return mayBeAlbumArt
                .map(albumArt -> ResponseEntity
                        .ok()
                        .cacheControl(CacheControl.maxAge(1, TimeUnit.HOURS))
                        .body(albumArt.resources().getResizedOrOriginal(size).byteArray()))
                .orElseGet(() -> ResponseEntity
                        .notFound()
                        .build());
    }

    @GetMapping(
            path = "/{albumId}/{albumFolderImageFileName}",
            produces = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE})
    ResponseEntity<byte[]> get (@PathVariable String albumId,
                                @PathVariable String albumFolderImageFileName,
                                @RequestParam(required = false, name = "size") ImageSizeRequestParam size) {
        var mayBeAlbumArt = albumArtService.get(albumId, albumFolderImageFileName);
        return mayBeAlbumArt
                .map(albumArt -> ResponseEntity
                        .ok()
                        .cacheControl(CacheControl.maxAge(1, TimeUnit.HOURS))
                        .body(albumArt.resources().getResizedOrOriginal(size).byteArray()))
                .orElseGet(() -> ResponseEntity
                        .notFound()
                        .build());
    }

}
