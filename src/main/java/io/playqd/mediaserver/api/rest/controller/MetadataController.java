package io.playqd.mediaserver.api.rest.controller;

import io.playqd.mediaserver.model.Artist;
import io.playqd.mediaserver.persistence.AudioFileDao;
import io.playqd.mediaserver.service.metadata.MetadataContentInfo;
import io.playqd.mediaserver.service.metadata.MediaMetadataService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/metadata")
class MetadataController {

    private final AudioFileDao audioFileDao;
    private final MediaMetadataService mediaMetadataService;

    MetadataController(AudioFileDao audioFileDao, MediaMetadataService mediaMetadataService) {
        this.audioFileDao = audioFileDao;
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

    @GetMapping("/artists")
    Page<Artist> artists(@PageableDefault(size = 100, sort = "name") Pageable page) {
        return audioFileDao.getArtists(page);
    }

    @GetMapping("/tracks")
    Page<?> tracks(@PageableDefault(size = 100, sort = "name") Pageable page) {
        return audioFileDao.getAudioFiles(page);
    }

}
