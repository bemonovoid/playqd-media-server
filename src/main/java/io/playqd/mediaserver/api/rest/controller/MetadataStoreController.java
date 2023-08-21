package io.playqd.mediaserver.api.rest.controller;

import io.playqd.mediaserver.service.metadata.MetadataContentInfo;
import io.playqd.mediaserver.service.metadata.MediaMetadataService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/metadata")
class MetadataStoreController {

    private final MediaMetadataService mediaMetadataService;

    MetadataStoreController(MediaMetadataService mediaMetadataService) {
        this.mediaMetadataService = mediaMetadataService;
    }

    @GetMapping("/sources/{sourceId}/info")
    MetadataContentInfo info(@PathVariable long sourceId) {
        return mediaMetadataService.getInfo(sourceId);
    }

    @DeleteMapping("/sources/{sourceId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    String clear(@PathVariable long sourceId) {
        return String.format("Successfully removed %s metadata items from store.", mediaMetadataService.clear(sourceId));
    }

}
